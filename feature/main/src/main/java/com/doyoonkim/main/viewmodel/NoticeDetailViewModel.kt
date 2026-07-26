package com.doyoonkim.main.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.analytics.AnalyticsLogger
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.abtest.aifeature.AiFeatureAbTestLayoutPolicy
import com.doyoonkim.domain.abtest.aifeature.GetAiFeatureTestLayoutPolicy
import com.doyoonkim.domain.usecases.FetchNoticeById
import com.doyoonkim.main.contract.AiFeatureAbTestLayoutState
import com.doyoonkim.main.contract.NoticeDetailEvent
import com.doyoonkim.main.contract.NoticeDetailMutation
import com.doyoonkim.main.contract.NoticeDetailSideEffect
import com.doyoonkim.main.contract.NoticeDetailState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeDetailViewModel @Inject constructor(
    private val fetchNoticeById: FetchNoticeById,
    private val layoutPolicy: GetAiFeatureTestLayoutPolicy,
    private val analytics: AnalyticsLogger
) : BaseViewModel<NoticeDetailState, NoticeDetailEvent, NoticeDetailSideEffect, NoticeDetailMutation>() {
    private val TAG = this.javaClass.name
    override fun setInitialState(): NoticeDetailState = NoticeDetailState()

    private var isTestActivated: Boolean = false

    init {
        // Fetch Remote configuration for AB test.
        // Get Policy information
        when (val layoutPolicy = layoutPolicy.getLayoutPolicy()) {
            is AiFeatureAbTestLayoutPolicy.VariantA -> {
                mutate(NoticeDetailMutation.TestLayoutVariantA(layoutPolicy.variantName))
                    .also { Log.d(TAG, "RECEIVED POLICY: $layoutPolicy") }
            }
            is AiFeatureAbTestLayoutPolicy.VariantB -> {
                mutate(NoticeDetailMutation.TestLayoutVariantB(layoutPolicy.variantName))
                    .also { Log.d(TAG, "RECEIVED POLICY: $layoutPolicy") }
            }
        }
    }

    override fun handleEvent(event: NoticeDetailEvent) {
        when (event) {
            is NoticeDetailEvent.RequestNotice -> {
                getTargetNoticeById(event.nttId, event.canBookmark)
            }
            is NoticeDetailEvent.UpdateLoadingStatus -> {
                updateLoadingStatus(event.status)
            }
            is NoticeDetailEvent.RequestBookmarkCreation -> {
                // AB Test related Logging
                analytics.logEvent(
                    "bookmark_button_action_clicked",
                    Bundle().apply {
                        putString("exp_variant", uiState.value.abTestLayoutState.layoutType)
                    }
                )
                with(uiState.value) {
                    if (isReceived) {
                        receivedNotice?.let {
                            sendSideEffect(
                                NoticeDetailSideEffect.NavToEditBookmark(it)
                            )
                        }
                    }
                }
            }
            is NoticeDetailEvent.RequestNoticeSummary -> {
                // AB Test related Logging
                analytics.logEvent(
                    "ai_button_action_clicked",
                    Bundle().apply {
                        putString("exp_variant", uiState.value.abTestLayoutState.layoutType)
                    }
                )

                with(uiState.value) {
                    if (!summarizedContent.isNullOrEmpty()) {
                        // TODO: Need to be revised.
                        mutate(NoticeDetailMutation.Summary.Success(summarizedContent))
                    } else {
                        if (isReceived) {
                            receivedNotice?.let {
                                getNoticeSummary(receivedNotice.nttId)
                            }
                        }
                    }
                }
            }
            is NoticeDetailEvent.DismissBottomSheet -> {
                mutate(NoticeDetailMutation.Summary.Dismiss)
            }
            is NoticeDetailEvent.RequestDownloadAttachment -> {
                if (!event.status) {
                    // Exception Occurred.
                    sendSideEffect(NoticeDetailSideEffect.ShowDownloadUnableToast)
                    return
                }

                // Download Successfully enqueued
                sendSideEffect(NoticeDetailSideEffect.ShowDownloadToast)
                sendSideEffect(NoticeDetailSideEffect.NavToDownload)
            }
            is NoticeDetailEvent.GoBack -> {
                sendSideEffect(NoticeDetailSideEffect.NavToBack)
            }
            // Analytics related Event. To be removed once test is completed.
            is NoticeDetailEvent.ActivateAbTest -> {
                if (!isTestActivated && !uiState.value.abTestLayoutState.layoutType.isBlank()) {
                    // Activate Test via Logger
                    analytics.logEvent(
                        "ai_feature_visible",
                        Bundle().apply {
                            putString("exp_variant", uiState.value.abTestLayoutState.layoutType)
                        }
                    )
                    isTestActivated = true
                }
            }
        }
    }

    private fun getTargetNoticeById(nttId: Int, canBookmark: Boolean) =
        viewModelScope.launch {
            fetchNoticeById(nttId)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { vo ->
                            mutate(NoticeDetailMutation.Notice.Success(vo, canBookmark))
                        },
                        onFailure = { reason ->
                            mutate(NoticeDetailMutation.Notice.Failure(reason.stackTraceToString()))
                        }
                    )
                }
        }

    private fun updateLoadingStatus(newStatus: Int) {
        viewModelScope.launch {
            mutate(NoticeDetailMutation.Content.Loading((newStatus / 100).toFloat()))

            if (newStatus == 100) {
                delay(600L)
                mutate(NoticeDetailMutation.Content.Success)
            }
        }
    }

    private fun getNoticeSummary(nttId: Int) {
        viewModelScope.launch {
            with(uiState.value) {
                if (!isSummaryProcessing) {
                    fetchNoticeById.getNoticeSummary(nttId).fold(
                        onSuccess = {
                            mutate(NoticeDetailMutation.Summary.Success(it))
                        },
                        onFailure = {
                            mutate(NoticeDetailMutation.Summary.Failure(it.stackTraceToString()))
                        }
                    )
                }
            }
        }
    }

    // Main Reducer
    override fun reduce(
        currentState: NoticeDetailState,
        mutation: NoticeDetailMutation
    ): NoticeDetailState {
        return when (mutation) {
            is NoticeDetailMutation.Notice -> {
                mutation.reducer(currentState)
            }
            is NoticeDetailMutation.Content -> {
                mutation.reducer(currentState)
            }
            is NoticeDetailMutation.Summary -> {
                mutation.reducer(currentState)
            }
            // AB Test related State modification. TO BE REMOVED once test is completed.
            is NoticeDetailMutation.TestLayoutVariantA -> {
                currentState.copy(
                    abTestLayoutState = AiFeatureAbTestLayoutState(
                        layoutType = mutation.name,
                        isAiFabBottomStart = true,
                        isAiFabBottomEnd = false
                    )
                )
            }
            is NoticeDetailMutation.TestLayoutVariantB -> {
                currentState.copy(
                    abTestLayoutState = AiFeatureAbTestLayoutState(
                        layoutType = mutation.name,
                        isAiFabBottomStart = false,
                        isAiFabBottomEnd = true
                    )
                )
            }
        }
    }

    // Specialized Reducer
    private fun NoticeDetailMutation.Notice.reducer(state: NoticeDetailState) =
        when (this) {
            is NoticeDetailMutation.Notice.Success -> {
                state.copy(
                    receivedNotice = notice,
                    isReceived = true,
                    isBookmarkButtonVisible = canBookmark
                )
            }
            is NoticeDetailMutation.Notice.Failure -> {
                state.copy(
                    receivedNotice = null,
                    isReceived = false,
                    isBookmarkButtonVisible = false
                ).also { Log.d(TAG, "FAILURE: $reason") }
            }
        }

    private fun NoticeDetailMutation.Content.reducer(state: NoticeDetailState) =
        when (this) {
            is NoticeDetailMutation.Content.Loading -> {
                state.copy(
                    loadingStatus = statue
                )
            }
            is NoticeDetailMutation.Content.Success -> {
                state.copy(
                    isLoadingCompleted = true
                )
            }
            is NoticeDetailMutation.Content.Failure -> {
                state.copy(
                    isLoadingCompleted = false
                ).also { Log.d(TAG, "LOADING FAILED on ${state.loadingStatus}\n$reason") }
            }
        }

    private fun NoticeDetailMutation.Summary.reducer(state: NoticeDetailState) =
        when (this) {
            is NoticeDetailMutation.Summary.Success -> {
                state.copy(
                    isSummaryProcessing = false,
                    isSummarizationVisible = true,
                    summarizedContent = content
                )
            }
            is NoticeDetailMutation.Summary.Failure -> {
                state.copy(
                    isSummaryProcessing = false,
                    isSummarizationVisible = false,
                    summarizedContent = null
                )
            }
            is NoticeDetailMutation.Summary.Loading -> {
                state.copy(
                    isSummaryProcessing = true
                )
            }
            is NoticeDetailMutation.Summary.Dismiss -> {
                state.copy(
                    isSummarizationVisible = false
                )
            }
        }
}
