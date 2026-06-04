package com.doyoonkim.bookmark.viewmodel

import android.util.Log
import com.doyoonkim.domain.SortOption
import androidx.lifecycle.viewModelScope
import com.doyoonkim.bookmark.contract.BookmarkListEvent
import com.doyoonkim.bookmark.contract.BookmarkListMutation
import com.doyoonkim.bookmark.contract.BookmarkListSideEffect
import com.doyoonkim.bookmark.contract.BookmarkListState
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.domain.interfaces.AppDatabasePreferenceRepository
import com.doyoonkim.domain.usecases.FetchAllBookmarks
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkListViewModel @Inject constructor(
    private val appDatabasePreference: AppDatabasePreferenceRepository,
    private val fetchAllBookmarks: FetchAllBookmarks
) : BaseViewModel<BookmarkListState, BookmarkListEvent, BookmarkListSideEffect, BookmarkListMutation>() {

    private val TAG = this.javaClass.name
    override fun setInitialState(): BookmarkListState {
        return BookmarkListState()
    }

    override fun handleEvent(event: BookmarkListEvent) {
        when (event) {
            is BookmarkListEvent.CheckSyncStatus -> {
                if (appDatabasePreference.isPartialFailedDuringDatabaseSync()) {
                    mutate(BookmarkListMutation.SyncNeeded)
                }
            }
            is BookmarkListEvent.RequestBookmark -> {
                mutate(BookmarkListMutation.Refreshing)
                requestBookmarks()
            }
            is BookmarkListEvent.RequestMoreBookmark -> requestBookmarks()
            is BookmarkListEvent.RequestBookmarkDetail -> {
                val dest = uiState.value.bookmarks[event.index].run {
                    BookmarkInfo(
                        noticeId = this.noticeId,
                        noticeTitle = this.noticeTitle,
                        noticeInfo = this.noticeCategory
                    )
                }
                sendSideEffect(BookmarkListSideEffect.NavTo(dest))
            }
            is BookmarkListEvent.RequestSettings -> sendSideEffect(BookmarkListSideEffect.Settings)
            is BookmarkListEvent.UpdateSortOption -> {
                if (uiState.value.bookmarks.isNotEmpty()) {
                    updateSortOption(event.option)
                    requestBookmarks()
                }
            }
        }
    }


    private fun requestBookmarks(
        size: Int = 20,
        pageNumber: Int = 0
    ) {
        viewModelScope.launch {
            mutate(BookmarkListMutation.Loading).also { delay(200L) }

            fetchAllBookmarks(
                size = size,
                pageNumber = uiState.value.pageNumber,
                option = uiState.value.sortOption
            ).collectLatest { result ->
                    result.fold(
                        onSuccess = { vo ->
                            mutate(BookmarkListMutation.Success(vo))
                        },
                        onFailure = { reason ->
                            mutate(BookmarkListMutation.Failure(reason.stackTraceToString()))
                        }
                    )
                }
        }
    }

    private fun updateSortOption(index: Int) {
        // When sort option is changed -> Fetch bookmark again from page 0.
        mutate(BookmarkListMutation.Sort(SortOption.entries[index]))
    }

    // Main Reducer
    override fun reduce(
        currentState: BookmarkListState,
        mutation: BookmarkListMutation
    ): BookmarkListState {
        return when (mutation) {
            is BookmarkListMutation.SyncNeeded -> { currentState.copy(isSyncRequired = true) }
            is BookmarkListMutation.Loading -> { currentState.copy(isLoading = true) }
            is BookmarkListMutation.Refreshing -> {
                currentState.copy(
                    isRefreshing = true,
                    bookmarks = emptyList(),
                    pageNumber = 0
                )
            }
            is BookmarkListMutation.Sort -> {
                currentState.copy(
                    sortOption = mutation.option,
                    bookmarks = emptyList(),
                    pageNumber = 0
                )
            }
            is BookmarkListMutation.Success -> {
                currentState.copy(
                    bookmarks = currentState.bookmarks.toMutableList().apply {
                        addAll(mutation.result)
                    }.distinctBy { e -> e.bookmarkId }.toList(),
                    pageNumber = currentState.pageNumber + 1,
                    isReachEnd = mutation.result.size < 20,
                    isLoading = false,
                    isRefreshing = false,
                    isError = false
                )
            }
            is BookmarkListMutation.Failure -> {
                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isError = true
                ).also { Log.d(TAG, "FAILURE: ${mutation.reason}") }
            }
        }
    }
}