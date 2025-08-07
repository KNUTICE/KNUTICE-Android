package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO

data class NoticesInCategoryState(
    val currentLastNttId: Int = 0,
    val notices: List<NoticeVO> = List(20) { NoticeVO() },
    val isNoticesRequested: Boolean = true,
    val isError: Boolean = false,
    val isLoading: Boolean = true,
    val isRefreshRequested: Boolean = false
): UiState

sealed class NoticesInCategoryEvent: UiEvent {
    data class RequestNotices(val category: NoticeCategory): NoticesInCategoryEvent()
    data class RequestNoticeDetail(val id: Int, val url: String): NoticesInCategoryEvent()
    data object RequestRefresh: NoticesInCategoryEvent()
    data object GoBack: NoticesInCategoryEvent()
}

sealed class NoticesInCategorySideEffect: UiSideEffect {
    data class NavToNoticeDetail(val id: Int, val url: String): NoticesInCategorySideEffect()
    data object NavToBack: NoticesInCategorySideEffect()
}