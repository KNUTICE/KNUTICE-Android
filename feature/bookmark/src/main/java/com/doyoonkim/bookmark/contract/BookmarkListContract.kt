package com.doyoonkim.bookmark.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.domain.SortOption
import com.doyoonkim.model.BookmarkAsListElementVO

data class BookmarkListState(
    val bookmarks: List<BookmarkAsListElementVO> = emptyList(),
    val isRefreshing: Boolean = false,
    val isRequested: Boolean = true,
    val isFetchingCompleted: Boolean = true,
    val sortOption: SortOption = SortOption.DES_CREATION,
    val pageNumber: Int = 0,
    val isReachEnd: Boolean = false,
    val isSyncRequired: Boolean = false
) : UiState

// Event
sealed class BookmarkListEvent : UiEvent {
    data object CheckSyncStatus: BookmarkListEvent()
    data object RequestBookmark : BookmarkListEvent()
    data object RequestMoreBookmark: BookmarkListEvent()
    data class RequestBookmarkDetail(val index: Int): BookmarkListEvent()
    data class UpdateSortOption(val option: Int): BookmarkListEvent()
}

// Side Effect
sealed class BookmarkListSideEffect : UiSideEffect {
    data class NavTo(val dest: BookmarkInfo) : BookmarkListSideEffect()
}