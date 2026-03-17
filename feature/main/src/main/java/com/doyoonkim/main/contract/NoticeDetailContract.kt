package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiMutation
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.model.MarkdownString
import com.doyoonkim.model.NoticeVO

data class NoticeDetailState(
    val receivedNotice: NoticeVO? = null,
    val isReceived: Boolean = false,
    val loadingStatus: Float = 0.0f,
    val isLoadingCompleted: Boolean = false,
    val isBookmarkButtonVisible: Boolean = false,
    val isSummarizationVisible: Boolean = false,
    val isSummaryProcessing: Boolean = false,
    val summarizedContent: List<MarkdownString>? = null,
    val abTestLayoutState: AiFeatureAbTestLayoutState = AiFeatureAbTestLayoutState()
) : UiState

// AB TEST related State. (To be removed once test is completed)
// Defined with default value (layout_a)
data class AiFeatureAbTestLayoutState(
    val layoutType: String = "",
    val isAiFabBottomStart: Boolean = true,
    val isAiFabBottomEnd: Boolean = false
)

sealed interface NoticeDetailEvent: UiEvent {
    data class RequestNotice(val nttId: Int, val canBookmark: Boolean): NoticeDetailEvent
    data class UpdateLoadingStatus(val status: Int): NoticeDetailEvent
    data object RequestBookmarkCreation: NoticeDetailEvent
    data object RequestNoticeSummary: NoticeDetailEvent
    data object RequestDownloadAttachment: NoticeDetailEvent
    data object DismissBottomSheet: NoticeDetailEvent
    data object GoBack: NoticeDetailEvent

    // AB TEST related event (To be removed once test is completed)
    data object ActivateAbTest: NoticeDetailEvent
}

sealed class NoticeDetailSideEffect: UiSideEffect {
    data class NavToEditBookmark(val target: NoticeVO): NoticeDetailSideEffect()
    data object NavToBack: NoticeDetailSideEffect()
    data object ShowDownloadToast: NoticeDetailSideEffect()
    data object NavToDownload: NoticeDetailSideEffect()
}

sealed interface NoticeDetailMutation: UiMutation {
    sealed interface Notice: NoticeDetailMutation {
        data class Success(val notice: NoticeVO, val canBookmark: Boolean): Notice
        data class Failure(val reason: String): Notice
    }
    sealed interface Content: NoticeDetailMutation {
        data class Loading(val statue: Float): Content
        data object Success: Content
        data class Failure(val reason: String): Content
    }
    sealed interface Summary: NoticeDetailMutation {
        data class Success(val content: List<MarkdownString>): Summary
        data class Failure(val reason: String): Summary
        data object Loading: Summary
        data object Dismiss: Summary
    }

    // AB Test related Feature. TO BE REMOVED once Test is completed.
    data class TestLayoutVariantA(val name: String): NoticeDetailMutation
    data class TestLayoutVariantB(val name: String): NoticeDetailMutation
}