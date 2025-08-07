package com.doyoonkim.main.viewmodel

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.domain.usecases.FetchTopicSubscriptionStatus
import com.doyoonkim.domain.usecases.SubmitNotificationPreferences
import com.doyoonkim.main.R
import com.doyoonkim.main.contract.NotificationPrefEvent
import com.doyoonkim.main.contract.NotificationPrefSideEffect
import com.doyoonkim.main.contract.NotificationPrefStatus
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationPreferencesViewModel @Inject constructor(
    private val submitNotificationPreferences: SubmitNotificationPreferences,
    private val fetchTopicSubscriptionStatus: FetchTopicSubscriptionStatus,
    private val notificationManager: NotificationManager,
    @ApplicationContext private val context: Context
) : BaseViewModel<NotificationPrefStatus, NotificationPrefEvent, NotificationPrefSideEffect>() {

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

        updateMainNotificationPermissionStatus(
            isNotificationAllowed && isChannelAllowed
        )
    }

    private val notificationChannels = hashMapOf(
        0 to NoticeCategory.GENERAL_NEWS,
        1 to NoticeCategory.ACADEMIC_NEWS,
        2 to NoticeCategory.SCHOLARSHIP_NEWS,
        3 to NoticeCategory.EVENT_NEWS,
        4 to NoticeCategory.EMPLOYMENT_NEWS
    )

    private fun updateMainNotificationPermissionStatus(status: Boolean) =
        stateUpdate {
            it.copy(
                isMainNotificationPermissionGranted = status
            )
        }

    private fun updateChannelPreferenceState(index: Int, state: Boolean) {
        stateUpdate {
            it.copy(
                isSyncCompleted = false
            )
        }

        // TODO: Consider using viewModelScope + Dispatcher Injection in Usecase.
        CoroutineScope(Dispatchers.IO).launch {
            val jobSubmit = launch {
                // Ignore the result.
                submitNotificationPreferences(
                    TopicSubscriptionPreferencesBody(
                        noticeName = notificationChannels[index]!!.name,
                        isSubscribed = state
                    )
                ).collectLatest { result ->
                    if (!result) {
                        stateUpdate {
                            it.copy(
                                isSyncCompleted = true,
                                isError = true
                            )
                        }
                    } else {
                        stateUpdate {
                            it.copy(
                                isEachChannelAllowed = it.isEachChannelAllowed.updateValueByIndex(index, state),
                                isSyncCompleted = true,
                                isError = false
                            )
                        }
                    }
                }
            }
            delay(5000L)
            if (!jobSubmit.isCompleted) jobSubmit.cancelAndJoin()
        }
    }

    private fun getTopicSubscriptionStatus() =
        viewModelScope.launch {
            fetchTopicSubscriptionStatus()
                .collectLatest { result ->
                    result.fold(
                        onSuccess =  { status ->
                            stateUpdate {
                                it.copy(
                                    isEachChannelAllowed = listOf(
                                        status.general,
                                        status.academic,
                                        status.scholarship,
                                        status.event,
                                        status.employment
                                    ),
                                    isSyncCompleted = true,
                                    isError = false
                                )
                            }
                        },
                        onFailure = {
                            stateUpdate {
                                it.copy(
                                    isSyncCompleted = true,
                                    isError = true
                                )
                            }
                        }
                    )
                }
        }

    private fun List<Boolean>.updateValueByIndex(index: Int, value: Boolean) =
        List(this.size) {
            if (it == index) value
            else this[it]
        }
}