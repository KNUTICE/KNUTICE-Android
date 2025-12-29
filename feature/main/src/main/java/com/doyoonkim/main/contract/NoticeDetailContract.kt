package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.model.NoticeVO

data class NoticeDetailState(
    val receivedNotice: NoticeVO? = null,
    val isReceived: Boolean = false,
    val loadingStatus: Float = 0.0f,
    val isLoadingCompleted: Boolean = false
) : UiState

sealed class NoticeDetailEvent: UiEvent {
    data class RequestNotice(val nttId: Int): NoticeDetailEvent()
    data class UpdateLoadingStatus(val status: Int): NoticeDetailEvent()
    data object RequestBookmarkCreation: NoticeDetailEvent()
    data object RequestDownloadAttachment: NoticeDetailEvent()
    data object GoBack: NoticeDetailEvent()
}

sealed class NoticeDetailSideEffect: UiSideEffect {
    data class NavToEditBookmark(val target: NoticeVO): NoticeDetailSideEffect()
    data object NavToBack: NoticeDetailSideEffect()
    data object ShowDownloadToast: NoticeDetailSideEffect()
    data object NavToDownload: NoticeDetailSideEffect()
}