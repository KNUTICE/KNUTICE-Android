package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.model.NoticeVO

data class NoticeSearchState(
    val searchKeyword: String = "",
    val isError: Boolean = false,
    val isFetching: Boolean = false,
    val canRequestMoreNotices: Boolean = true,
    val fetchResult: List<NoticeVO> = emptyList()
) : UiState

sealed class NoticeSearchEvent : UiEvent {
    data class InitiateSearch(val keyword: String): NoticeSearchEvent()
    data class UpdateSearchKeyword(val value: String): NoticeSearchEvent()
    data object RequestMoreNotices: NoticeSearchEvent()
    data class RequestNoticeDetail(val id: Int, val url: String): NoticeSearchEvent()
    data object GoBack: NoticeSearchEvent()
}

sealed class NoticeSearchSideEffect: UiSideEffect {
    data class NavToNoticeDetail(val id: Int, val url: String): NoticeSearchSideEffect()
    data object NavToBack: NoticeSearchSideEffect()
}