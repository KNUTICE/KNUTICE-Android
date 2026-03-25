package com.doyoonkim.main.viewmodel

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.interfaces.AppPreferenceRepository
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.domain.usecases.FetchTopicSubscriptionStatus
import com.doyoonkim.domain.usecases.SubmitNotificationPreferences
import com.doyoonkim.main.contract.NotificationPrefEvent
import com.doyoonkim.main.contract.NotificationPrefMutation
import com.doyoonkim.main.contract.NotificationPrefSideEffect
import com.doyoonkim.main.contract.NotificationPrefStatus
import com.doyoonkim.model.MealCategory
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.TopicType
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationPreferencesViewModel @Inject constructor(
    private val submitNotificationPreferences: SubmitNotificationPreferences,
    private val fetchTopicSubscriptionStatus: FetchTopicSubscriptionStatus,
    private val notificationManager: NotificationManager,
    private val appPreferences: AppPreferenceRepository,
    @ApplicationContext private val context: Context
) : BaseViewModel<NotificationPrefStatus, NotificationPrefEvent, NotificationPrefSideEffect, NotificationPrefMutation>() {
    private val TAG = this.javaClass.name
    override fun setInitialState(): NotificationPrefStatus = NotificationPrefStatus()

    override fun handleEvent(event: NotificationPrefEvent) {
        when (event) {
            is NotificationPrefEvent.CheckMainPermissionStatus -> {
                checkMainPermissionStatus()
            }
            is NotificationPrefEvent.RequestSystemSettings -> {
                sendSideEffect(NotificationPrefSideEffect.NavToSystemSettings)
            }
            is NotificationPrefEvent.RequestTopicSubscriptionStatus -> {
                getTopicSubscriptionStatus()
            }
            is NotificationPrefEvent.UpdateSubscriptionStatus -> {
                with (event) {
                    updateChannelPreferenceState(index, value)
                }
            }
            is NotificationPrefEvent.UpdateMajorSubscriptionStatue -> {
                with (event) {
                    updateMajorSubscriptionStatus(value)
                }
            }
            is NotificationPrefEvent.UpdateMealSubscriptionStatus -> {
                with(event) {
                    updateMealSubscriptionStatus(idx, value)
                }
            }
            is NotificationPrefEvent.GoBack -> {
                if (uiState.value.isSyncCompleted)
                    sendSideEffect(NotificationPrefSideEffect.NavToBack)
            }
        }
    }

    // Check Main Notification Status
    private fun checkMainPermissionStatus() {
        val isNotificationAllowed = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        val isChannelAllowed = notificationManager
            .getNotificationChannel(
                context.getString(com.doyoonkim.common.R.string.inapp_notification_channel_id)
            ).importance > 0

        mutate(NotificationPrefMutation.MainPermission(isNotificationAllowed && isChannelAllowed))
    }

    private fun updateChannelPreferenceState(index: Int, state: Boolean) {
        mutate(NotificationPrefMutation.Syncing)

        viewModelScope.launch {
            // Ignore the result.
            submitNotificationPreferences(
                TopicSubscriptionPreferencesBody(
                    topicType = TopicType.NOTICE,
                    noticeName = NoticeCategory.entries[index].name,
                    isSubscribed = state
                )
            ).collectLatest { result ->
                if (result) {
                    mutate(NotificationPrefMutation.Notice.UpdateSuccess(index, state))
                } else {
                    mutate(NotificationPrefMutation.Notice.Failure("Submit Failed"))
                }
            }
        }.invokeOnCompletion { mutate(NotificationPrefMutation.Synced) }
    }

    private fun updateMajorSubscriptionStatus(state: Boolean) {
        val subscribedMajor = appPreferences.getSubscribedMajor() ?: return
        mutate(NotificationPrefMutation.Syncing)

        viewModelScope.launch {
            submitNotificationPreferences.invoke(
                TopicSubscriptionPreferencesBody(
                    topicType = TopicType.MAJOR,
                    noticeName = subscribedMajor,
                    isSubscribed = state
                )
            ).collectLatest { result ->
                if (result) {
                    mutate(NotificationPrefMutation.Major.UpdateSuccess(state))
                } else {
                    mutate(NotificationPrefMutation.Major.Failure("Submit Failed"))
                }
            }
        }.invokeOnCompletion { mutate(NotificationPrefMutation.Synced) }
    }

    // Meal
    private fun updateMealSubscriptionStatus(idx: Int, state: Boolean) {
        mutate(NotificationPrefMutation.Syncing)

        viewModelScope.launch {
            submitNotificationPreferences.invoke(
                TopicSubscriptionPreferencesBody(
                    topicType = TopicType.MEAL,
                    noticeName = MealCategory.entries[idx].name,
                    isSubscribed = state
                )
            ).collectLatest { result ->
                if (result) mutate(NotificationPrefMutation.Meal.UpdateSuccess(idx, state))
                else mutate(NotificationPrefMutation.Meal.Failure("Update Failed."))
            }
        }.invokeOnCompletion{ mutate(NotificationPrefMutation.Synced) }
    }

    private fun getTopicSubscriptionStatus() =
        viewModelScope.launch {
            mutate(NotificationPrefMutation.Syncing)
            fetchTopicSubscriptionStatus(TopicType.NOTICE)
                .collectLatest { result ->
                    result.fold(
                        onSuccess =  { status ->
                            mutate(
                                NotificationPrefMutation.Notice.FetchSuccess(
                                    status.map { value -> value.second }
                                )
                            )
                        },
                        onFailure = { reason ->
                            mutate(
                                NotificationPrefMutation.Notice.Failure(reason.stackTraceToString())
                            )
                        }
                    )
                }

            fetchTopicSubscriptionStatus(TopicType.MAJOR)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { status ->
                            val cachedMajorSelection = appPreferences.getSubscribedMajor()
                            if (cachedMajorSelection != null) {
                                var majorStatus = false
                                if (status.isNotEmpty()) {
                                    if (cachedMajorSelection != status.first().first) {
                                        appPreferences.updateSubscribedMajor(status.first().first)
                                    }
                                    majorStatus = status.first().second
                                }
                                mutate(NotificationPrefMutation.Major.FetchSuccess(majorStatus))
                            }
                        },
                        onFailure = { reason ->
                            mutate(
                                NotificationPrefMutation.Major.Failure(reason.stackTraceToString())
                            )
                        }
                    )
                }

            fetchTopicSubscriptionStatus(TopicType.MEAL)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            mutate(NotificationPrefMutation.Meal.FetchSuccess(
                                status = it.map { value -> value.second }
                            ))
                        },
                        onFailure = {
                            mutate(NotificationPrefMutation.Meal.Failure(it.stackTraceToString()))
                        }
                    )
                }
        }.invokeOnCompletion { mutate(NotificationPrefMutation.Synced) }

    private fun List<Boolean>.updateValueByIndex(index: Int, value: Boolean) =
        List(this.size) {
            if (it == index) value
            else this[it]
        }

    // Main Reducer
    override fun reduce(
        currentState: NotificationPrefStatus,
        mutation: NotificationPrefMutation
    ): NotificationPrefStatus {
        return when (mutation) {
            is NotificationPrefMutation.Syncing ->  { currentState.copy(isSyncCompleted = false) }
            is NotificationPrefMutation.Synced -> { currentState.copy(isSyncCompleted = true) }
            is NotificationPrefMutation.MainPermission -> {
                currentState.copy(
                    isMainNotificationPermissionGranted = mutation.status
                )
            }
            is NotificationPrefMutation.Notice -> { mutation.reducer(currentState) }
            is NotificationPrefMutation.Major -> { mutation.reducer(currentState) }
            is NotificationPrefMutation.Meal -> { mutation.reducer(currentState) }
        }
    }

    // Specialized Reducer
    private fun NotificationPrefMutation.Notice.reducer(state: NotificationPrefStatus) =
        when (this) {
            is NotificationPrefMutation.Notice.FetchSuccess -> {
                state.copy(
                    isEachChannelAllowed = status,
                    isError = false
                )
            }
            is NotificationPrefMutation.Notice.UpdateSuccess -> {
                state.copy(
                    isEachChannelAllowed = state.isEachChannelAllowed.updateValueByIndex(
                        index, status
                    ),
                    isError = false
                )
            }
            is NotificationPrefMutation.Notice.Failure -> {
                state.copy(
                    isError = true
                ).also { Log.d(TAG, "FAILURE: $reason") }
            }
        }

    private fun NotificationPrefMutation.Major.reducer(state: NotificationPrefStatus) =
        when (this) {
            is NotificationPrefMutation.Major.FetchSuccess -> {
                state.copy(
                    isMajorSubscribed = true,
                    isMajorChannelAllowed = status,
                    isError = false
                )
            }
            is NotificationPrefMutation.Major.UpdateSuccess -> {
                state.copy(
                    isMajorChannelAllowed = status,
                    isError = false
                )
            }
            is NotificationPrefMutation.Major.Failure -> {
                state.copy(
                    isError = true
                ).also { Log.d(TAG, "FAILURE: $reason") }
            }
        }

    private fun NotificationPrefMutation.Meal.reducer(state: NotificationPrefStatus) =
        when (this) {
            is NotificationPrefMutation.Meal.FetchSuccess -> {
                state.copy(
                    isMealChannelAllowed = status,
                    isError = false
                )
            }
            is NotificationPrefMutation.Meal.UpdateSuccess -> {
                state.copy(
                    isMealChannelAllowed = state.isMealChannelAllowed.updateValueByIndex(
                        idx, status
                    ),
                    isError = false
                )
            }
            is NotificationPrefMutation.Meal.Failure -> {
                state.copy(isError = true).also { Log.d(TAG, "FAILURE: $reason") }
            }
        }
}