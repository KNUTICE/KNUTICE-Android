package com.doyoonkim.bookmark.viewmodel

import com.doyoonkim.domain.SortOption
import androidx.lifecycle.viewModelScope
import com.doyoonkim.bookmark.contract.BookmarkListEvent
import com.doyoonkim.bookmark.contract.BookmarkListSideEffect
import com.doyoonkim.bookmark.contract.BookmarkListState
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.domain.usecases.FetchAllBookmarks
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkListViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val fetchAllBookmarks: FetchAllBookmarks
) : BaseViewModel<BookmarkListState, BookmarkListEvent, BookmarkListSideEffect>() {

    init {
        // Preprocessing
        sendUiEvent(BookmarkListEvent.CheckSyncStatus)
        sendUiEvent(BookmarkListEvent.RequestBookmark)
    }

    override fun setInitialState(): BookmarkListState {
        return BookmarkListState()
    }

    override fun handleEvent(event: BookmarkListEvent) {
        when (event) {
            is BookmarkListEvent.CheckSyncStatus -> {
                if (appPreferences.isPartialFailedDuringDatabaseSync()) {
                    stateUpdate {
                        it.copy(isSyncRequired = true)
                    }
                }
            }
            is BookmarkListEvent.RequestBookmark -> requestBookmarks()
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
            updateFetchingStatus(false).also { delay(200L) }

            fetchAllBookmarks(
                size = size,
                pageNumber = uiState.value.pageNumber,
                option = uiState.value.sortOption
            ).collectLatest { result ->
                    result.fold(
                        onSuccess = { vo ->
                            stateUpdate {
                                it.copy(
                                    bookmarks = it.bookmarks.toMutableList().apply {
                                        this.addAll(vo)
                                    }.distinctBy { i -> i.bookmarkId }.toList(),
                                    pageNumber = it.pageNumber + 1,
                                    isRequested = false,
                                    isReachEnd = vo.size < size
                                )
                            }
                        },
                        onFailure = {
                            updateFetchingStatus(true)
                        }
                    )
                }
        }
    }


    private fun updateFetchingStatus(status: Boolean) =
        stateUpdate {
            it.copy(
                isFetchingCompleted = status
            )
        }

    private fun updateSortOption(index: Int) {
        // When sort option is changed -> Fetch bookmark again from page 0.
        stateUpdate {
            it.copy(
                sortOption = SortOption.entries[index],
                bookmarks = emptyList(),
                isRequested = true,
                pageNumber = 0
            )
        }
    }
}