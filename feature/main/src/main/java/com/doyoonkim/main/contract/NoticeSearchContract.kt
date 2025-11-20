package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.NoticeVO

data class NoticeSearchState(
    val searchKeyword: String = "",
    val fetchingSource: FetchingSource = FetchingSource.REMOTE,
    val isError: Boolean = false,
    val isFetching: Boolean = false,
    val isSearchResultEmpty: Boolean = false,
    val canRequestMoreNotices: Boolean = true,
    val fetchResult: List<NoticeVO> = emptyList(),
    val localFetchResult: List<BookmarkAsListElementVO> = emptyList()
) : UiState

enum class FetchingSource { REMOTE, LOCAL }

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