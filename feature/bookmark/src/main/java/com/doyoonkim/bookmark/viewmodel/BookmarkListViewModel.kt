package com.doyoonkim.bookmark.viewmodel

import com.doyoonkim.domain.SortOption
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchAllBookmarks
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkListViewModel @Inject constructor(
    private val fetchAllBookmarks: FetchAllBookmarks
) : ViewModel() {

    private var _uiState = MutableStateFlow(BookmarkListState())
    val uiState = _uiState.asStateFlow()

    fun requestBookmarks(
        size: Int = 20,
        pageNumber: Int = 0
    ) {
        viewModelScope.launch {
            updateFetchingStatus(false).also { delay(200L) }
            val receivedPage = mutableListOf<Pair<BookmarkVO, NoticeVO>>()

            fetchAllBookmarks(size = size, pageNumber = pageNumber, option = uiState.value.sortOption)
                .flowOn(Dispatchers.IO)
                .onCompletion { e ->
                    if (e == null) {
                        if (receivedPage.size == 0) {
                            _uiState.update {
                                it.copy(
                                    isRequested = false,
                                    isReachEnd = true
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    bookmarks = it.bookmarks.toMutableList().apply {
                                        this.addAll(receivedPage)
                                    }.distinctBy { e -> e.first.bookmarkId }.toList(),
                                    pageNumber = it.pageNumber + 1,
                                    isRequested = false,
                                    isReachEnd = receivedPage.size % size != 0
                                )
                            }
                        }
                    } else {
                        Log.d("BookmarkListViewModel", "Completed with error")
                        updateFetchingStatus(true)
                    }
                }
                .collectLatest { result -> receivedPage.add(result) }
        }
    }

    fun updateBookmarkRequestStatus(status: Boolean) =
        _uiState.update {
            it.copy(
                isRequested = status
            )
        }


    private fun updateFetchingStatus(status: Boolean) =
        _uiState.update {
            it.copy(
                isFetchingCompleted = status
            )
        }

    private fun updateStateOnFetchComplete(pageNumber: Int, isReachEnd: Boolean) =
        _uiState.update {
            it.copy(
                isFetchingCompleted = true,
                pageNumber = pageNumber,
                isReachEnd = isReachEnd,
                isRequested = false
            )
        }

    fun updateSortOption(index: Int) {
        // When sort option is changed -> Fetch bookmark again from page 0.
        _uiState.update {
            it.copy(
                sortOption = SortOption.entries[index],
                bookmarks = emptyList(),
                isRequested = true,
                pageNumber = 0
            )
        }
    }

}

data class BookmarkListState(
    val bookmarks: List<Pair<BookmarkVO, NoticeVO>> = emptyList(),
    val isRefreshing: Boolean = false,
    val isRequested: Boolean = true,
    val isFetchingCompleted: Boolean = true,
    val sortOption: SortOption = SortOption.DES_CREATION,
    val pageNumber: Int = 0,
    val isReachEnd: Boolean = false
)