package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.domain.usecases.FetchNoticesPerPage
import com.doyoonkim.domain.usecases.SubmitNotificationPreferences
import com.doyoonkim.main.contract.NoticeByMajorEvent
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
    private val appPreference: AppPreferences,
    private val fetchNoticesPerPage: FetchNoticesPerPage,
    private val submitNotificationPreferences: SubmitNotificationPreferences
): BaseViewModel<NoticeByMajorState, NoticeByMajorEvent, NoticeByMajorSideEffect>() {
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
                stateUpdate { it.copy(isBottomSheetVisible = true) }
            }
            is NoticeByMajorEvent.RequestBottomSheetHidden -> {
                stateUpdate { it.copy(isBottomSheetVisible = false) }
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
        val currentSubscription = appPreference.getSubscribedMajor()
        currentSubscription?.let { subscribed ->
            stateUpdate {
                it.copy(
                    targetMajor = MajorCategory.valueOf(subscribed),
                    isLoading = true
                )
            }
        }
        viewModelScope.launch {
            fetchNotice()
        }.invokeOnCompletion {
            stateUpdate {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    // Need to revised later.
    private fun updateSubscribedMajor(newSubscription: MajorCategory) {
        stateUpdate {
            it.copy(isLoading = true)
        }

        val prevSubscription = appPreference.getSubscribedMajor()
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
                    appPreference.updateSubscribedMajor(newSubscription.name)

                    stateUpdate {
                        it.copy(
                            lastNttId = 0,
                            notices = emptyList(),
                            targetMajor = newSubscription,
                            isFetchable = true
                        )
                    }
                    // Fetch new notices
                    sendUiEvent(NoticeByMajorEvent.RequestNotice)
                }
            }
        }.invokeOnCompletion {
            stateUpdate {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    private fun refreshNotices() {
        stateUpdate {
            it.copy(
                lastNttId = 0,
                notices = emptyList(),
                isRefreshing = true,
                isFetchable = true
            )
        }
        viewModelScope.launch {
            fetchNotice()
        }.invokeOnCompletion {
            stateUpdate {
                it.copy(
                    isRefreshing = false
                )
            }
        }
    }

    private fun loadNotices() {
        viewModelScope.launch {
            fetchNotice()
        }
    }

    private suspend fun fetchNotice() {
        Log.d(this.javaClass.name,"Fetching Notices for major ${uiState.value.targetMajor}")
        if (!uiState.value.isFetchable) return
        if (uiState.value.targetMajor != MajorCategory.UNSPECIFIED) {
            fetchNoticesPerPage.invoke(
                category = uiState.value.targetMajor.name, lastNttId = uiState.value.lastNttId
            ).collectLatest { result ->
                result.fold(
                    onSuccess = { notices ->
                        val updated = uiState.value.notices.toMutableList().apply {
                            addAll(notices)
                        }
                        stateUpdate {
                            it.copy(
                                lastNttId = updated.last().nttId,
                                notices = updated
                            )
                        }
                    },
                    onFailure = {
                        // Do nothing.
                        Log.d("NoticeByMajorViewModel", "Unable to retrieve notices")
                        stateUpdate {
                            it.copy(
                                isFetchable = false
                            )
                        }
                    }
                )
            }
        }
    }

}