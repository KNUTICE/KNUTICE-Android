package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.usecases.FetchNoticeById
import com.doyoonkim.main.contract.NoticeDetailEvent
import com.doyoonkim.main.contract.NoticeDetailMutation
import com.doyoonkim.main.contract.NoticeDetailSideEffect
import com.doyoonkim.main.contract.NoticeDetailState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeDetailViewModel @Inject constructor(
    private val fetchNoticeById: FetchNoticeById
) : BaseViewModel<NoticeDetailState, NoticeDetailEvent, NoticeDetailSideEffect, NoticeDetailMutation>() {
    private val TAG = this.javaClass.name
    override fun setInitialState(): NoticeDetailState = NoticeDetailState()

    override fun handleEvent(event: NoticeDetailEvent) {
        when (event) {
            is NoticeDetailEvent.RequestNotice -> {
                getTargetNoticeById(event.nttId)
            }
            is NoticeDetailEvent.UpdateLoadingStatus -> {
                updateLoadingStatus(event.status)
            }
            is NoticeDetailEvent.RequestBookmarkCreation -> {
                with (uiState.value) {
                    if (isReceived) receivedNotice?.let {
                        sendSideEffect(
                            NoticeDetailSideEffect.NavToEditBookmark(it)
                        )
                    }
                }
            }
            is NoticeDetailEvent.RequestNoticeSummary -> {
                with (uiState.value) {
                    if (!summarizedContent.isNullOrEmpty()) {
                        // TODO: Need to be revised.
                        mutate(NoticeDetailMutation.Summary.Success(summarizedContent))
                    } else {
                        if (isReceived)
                            receivedNotice?.let {
                                getNoticeSummary(receivedNotice.nttId)
                            }
                    }
                }
            }
            is NoticeDetailEvent.DismissBottomSheet -> {
                mutate(NoticeDetailMutation.Summary.Dismiss)
            }
            is NoticeDetailEvent.RequestDownloadAttachment -> {
                sendSideEffect(NoticeDetailSideEffect.ShowDownloadToast)
                sendSideEffect(NoticeDetailSideEffect.NavToDownload)
            }
            is NoticeDetailEvent.GoBack -> {
                sendSideEffect(NoticeDetailSideEffect.NavToBack)
            }
        }
    }

    private fun getTargetNoticeById(nttId:Int) =
        viewModelScope.launch {
            fetchNoticeById(nttId)
                .collectLatest { result ->
                    result.fold(
                        onSuccess =  { vo ->
                            mutate(NoticeDetailMutation.Notice.Success(vo))
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
            is NoticeDetailMutation.Notice -> { mutation.reducer(currentState) }
            is NoticeDetailMutation.Content -> { mutation.reducer(currentState) }
            is NoticeDetailMutation.Summary -> { mutation.reducer(currentState) }
        }
    }

    // Specialized Reducer
    private fun NoticeDetailMutation.Notice.reducer(state: NoticeDetailState) =
        when (this) {
            is NoticeDetailMutation.Notice.Success -> {
                state.copy(
                    receivedNotice = notice,
                    isReceived = true
                )
            }
            is NoticeDetailMutation.Notice.Failure -> {
                state.copy(
                    receivedNotice = null,
                    isReceived = false
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
