package com.doyoonkim.bookmark.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiMutation
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.domain.SortOption
import com.doyoonkim.model.BookmarkAsListElementVO

data class BookmarkListState(
    val bookmarks: List<BookmarkAsListElementVO> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val sortOption: SortOption = SortOption.DES_CREATION,
    val pageNumber: Int = 0,
    val isReachEnd: Boolean = false,
    val isSyncRequired: Boolean = false
) : UiState

// Event
sealed interface BookmarkListEvent : UiEvent {
    data object CheckSyncStatus : BookmarkListEvent
    data object RequestBookmark : BookmarkListEvent
    data object RequestMoreBookmark : BookmarkListEvent
    data class RequestBookmarkDetail(val index: Int) : BookmarkListEvent
    data class UpdateSortOption(val option: Int) : BookmarkListEvent
    data object RequestSettings : BookmarkListEvent
}

// Side Effect
sealed class BookmarkListSideEffect : UiSideEffect {
    data class NavTo(val dest: BookmarkInfo) : BookmarkListSideEffect()
    data object Settings : BookmarkListSideEffect()
}

sealed interface BookmarkListMutation : UiMutation {
    data object SyncNeeded : BookmarkListMutation
    data object Loading : BookmarkListMutation
    data object Refreshing : BookmarkListMutation
    data class Sort(val option: SortOption) : BookmarkListMutation
    data class Success(val result: List<BookmarkAsListElementVO>) : BookmarkListMutation
    data class Failure(val reason: String) : BookmarkListMutation
}
