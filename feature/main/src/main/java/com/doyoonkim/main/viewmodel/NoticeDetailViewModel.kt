package com.doyoonkim.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.usecases.FetchNoticeById
import com.doyoonkim.main.contract.NoticeDetailEvent
import com.doyoonkim.main.contract.NoticeDetailSideEffect
import com.doyoonkim.main.contract.NoticeDetailState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeDetailViewModel @Inject constructor(
    private val fetchNoticeById: FetchNoticeById
) : BaseViewModel<NoticeDetailState, NoticeDetailEvent, NoticeDetailSideEffect>() {

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
                    result.onSuccess { vo ->
                        stateUpdate {
                            it.copy(
                                receivedNotice = vo,
                                isReceived = true
                            )
                        }
                    }
                }
        }

    private fun updateLoadingStatus(newStatus: Int) {
        viewModelScope.launch {
            stateUpdate {
                it.copy(
                    loadingStatus = (newStatus / 100).toFloat()
                )
            }

            if (newStatus == 100) {
                delay(1000L)
                stateUpdate {
                    it.copy(
                        isLoadingCompleted = true
                    )
                }
            }
        }
    }
}
