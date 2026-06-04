package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.interfaces.AppSubscriptionPreferenceRepository
import com.doyoonkim.domain.usecases.FetchNoticesPerPage
import com.doyoonkim.domain.usecases.SubmitNotificationPreferences
import com.doyoonkim.main.contract.NoticeByMajorEvent
import com.doyoonkim.main.contract.NoticeByMajorMutation
import com.doyoonkim.main.contract.NoticeByMajorSideEffect
import com.doyoonkim.main.contract.NoticeByMajorState
import com.doyoonkim.model.MajorCategory
import com.doyoonkim.model.TopicType
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeByMajorViewModel @Inject constructor(
    private val appSubscriptionPreference: AppSubscriptionPreferenceRepository,
    private val fetchNoticesPerPage: FetchNoticesPerPage,
    private val submitNotificationPreferences: SubmitNotificationPreferences
): BaseViewModel<NoticeByMajorState, NoticeByMajorEvent, NoticeByMajorSideEffect, NoticeByMajorMutation>() {
    private val TAG = this.javaClass.name
    override fun setInitialState(): NoticeByMajorState = NoticeByMajorState(0)

    override fun handleEvent(event: NoticeByMajorEvent) {
        when (event) {
            is NoticeByMajorEvent.CheckSubscribedMajor -> {
                initialLoad()
            }
            is NoticeByMajorEvent.RequestRefresh -> {
                refreshNotices()
            }
            is NoticeByMajorEvent.RequestSettings -> {
                sendSideEffect(NoticeByMajorSideEffect.NavToSettings)
            }
            is NoticeByMajorEvent.RequestNotice -> {
                loadNotices()
            }
            is NoticeByMajorEvent.RequestBottomSheetExpand -> {
                mutate(NoticeByMajorMutation.BottomSheetStatus(true))
            }
            is NoticeByMajorEvent.RequestBottomSheetHidden -> {
                mutate(NoticeByMajorMutation.BottomSheetStatus(false))
            }
            is NoticeByMajorEvent.RequestGoBack -> {
                sendSideEffect(NoticeByMajorSideEffect.GoBack)
            }
            is NoticeByMajorEvent.RequestNoticeDetail -> {
                sendSideEffect(
                    NoticeByMajorSideEffect.NavToNotice(
                        event.notice.nttId,
                        event.notice.url
                    )
                )
            }
            is NoticeByMajorEvent.RequestMajorSubscriptionChange -> {
                updateSubscribedMajor(event.newTarget)
            }
        }
    }

    private fun initialLoad() {
        val currentSubscription = appSubscriptionPreference.getSubscribedMajor()
        currentSubscription?.let { subscribed ->
            mutate(NoticeByMajorMutation.Subscribed(MajorCategory.valueOf(subscribed)))
        }
        viewModelScope.launch {
            fetchNotice()
        }
    }

    // Need to revised later.
    private fun updateSubscribedMajor(newSubscription: MajorCategory) {
        mutate(NoticeByMajorMutation.Notices.Loading)

        val prevSubscription = appSubscriptionPreference.getSubscribedMajor()
        // State Update & Request subscription update on Remote Source.
        viewModelScope.launch {
            val subscribeNewTopic = submitNotificationPreferences.invoke(
                TopicSubscriptionPreferencesBody(
                    topicType = TopicType.MAJOR,
                    noticeName = newSubscription.name,
                    isSubscribed = true
                )
            ).first()

            if (subscribeNewTopic) {
                val unsubscribePrevTopic = prevSubscription?.let { prev ->
                    submitNotificationPreferences.invoke(
                        TopicSubscriptionPreferencesBody(
                            topicType = TopicType.MAJOR,
                            noticeName = prev,
                            isSubscribed = false
                        )
                    ).first()
                } ?: true

                if (unsubscribePrevTopic) {
                    // update
                    appSubscriptionPreference.updateSubscribedMajor(newSubscription.name)
                    mutate(NoticeByMajorMutation.Subscribed(newSubscription))
                    // Fetch new notices
                    loadNotices()
                }
            }
        }
    }

    private fun refreshNotices() {
        mutate(NoticeByMajorMutation.Notices.Refreshing)
        viewModelScope.launch {
            fetchNotice()
        }
    }

    private fun loadNotices() {
        viewModelScope.launch {
            fetchNotice()
        }
    }

    private suspend fun fetchNotice() {
        Log.d(TAG,"Fetching Notices for major ${uiState.value.targetMajor}")
        if (!uiState.value.isFetchable) {
            mutate(NoticeByMajorMutation.Notices.Failure("Unable to Fetch (Not Fetchable)"))
            return
        }

        mutate(NoticeByMajorMutation.Notices.Loading)
        if (uiState.value.targetMajor != MajorCategory.UNSPECIFIED) {
            fetchNoticesPerPage.invoke(
                category = uiState.value.targetMajor.name, lastNttId = uiState.value.lastNttId
            ).collectLatest { result ->
                result.fold(
                    onSuccess = { notices ->
                        val updated = uiState.value.notices.toMutableList().apply {
                            addAll(notices)
                        }
                        mutate(NoticeByMajorMutation.Notices.Success(updated))
                    },
                    onFailure = {
                        // Do nothing.
                        mutate(NoticeByMajorMutation.Notices.Failure("Unable to retrieve notices"))
                    }
                )
            }
        }
    }

    // Main Reducer
    override fun reduce(
        currentState: NoticeByMajorState,
        mutation: NoticeByMajorMutation
    ): NoticeByMajorState {
        return when (mutation) {
            is NoticeByMajorMutation.Notices -> { mutation.reducer(currentState) }
            is NoticeByMajorMutation.Subscribed -> {
                currentState.copy(
                    lastNttId = 0,
                    notices = emptyList(),
                    targetMajor = mutation.topic,
                    isFetchable = true
                )
            }
            is NoticeByMajorMutation.BottomSheetStatus -> {
                currentState.copy(
                    isBottomSheetVisible = mutation.expanded
                )
            }
        }
    }

    // Specialized Reducer
    private fun NoticeByMajorMutation.Notices.reducer(state: NoticeByMajorState) =
        when (this) {
            is NoticeByMajorMutation.Notices.Loading -> { state.copy(isLoading = true) }
            is NoticeByMajorMutation.Notices.Refreshing -> {
                state.copy(
                    isRefreshing = true,
                    isFetchable = true,
                    lastNttId = 0,
                    notices = emptyList()
                )
            }
            is NoticeByMajorMutation.Notices.Success -> {
                state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    notices = notices,
                    lastNttId = notices.last().nttId
                )
            }
            is NoticeByMajorMutation.Notices.Failure -> {
                state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isFetchable = false
                ).also { Log.d(TAG, "FAILURE: ${this.reason}") }
            }
        }

}