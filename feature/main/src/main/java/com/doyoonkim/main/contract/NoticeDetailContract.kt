package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiMutation
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.model.NoticeVO

data class NoticeDetailState(
    val receivedNotice: NoticeVO? = null,
    val isReceived: Boolean = false,
    val loadingStatus: Float = 0.0f,
    val isLoadingCompleted: Boolean = false
) : UiState

sealed interface NoticeDetailEvent: UiEvent {
    data class RequestNotice(val nttId: Int): NoticeDetailEvent
    data class UpdateLoadingStatus(val status: Int): NoticeDetailEvent
    data object RequestBookmarkCreation: NoticeDetailEvent
    data object RequestDownloadAttachment: NoticeDetailEvent
    data object GoBack: NoticeDetailEvent
}

sealed class NoticeDetailSideEffect: UiSideEffect {
    data class NavToEditBookmark(val target: NoticeVO): NoticeDetailSideEffect()
    data object NavToBack: NoticeDetailSideEffect()
    data object ShowDownloadToast: NoticeDetailSideEffect()
    data object NavToDownload: NoticeDetailSideEffect()
}

sealed interface NoticeDetailMutation: UiMutation {
    sealed interface Notice: NoticeDetailMutation {
        data class Success(val notice: NoticeVO): Notice
        data class Failure(val reason: String): Notice
    }
    sealed interface Content: NoticeDetailMutation {
        data class Loading(val statue: Float): Content
        data object Success: Content
        data class Failure(val reason: String): Content
    }
}