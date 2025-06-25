package com.doyoonkim.bookmark.viewmodel

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
            fetchAllBookmarks(size = size, pageNumber = pageNumber)
                .flowOn(Dispatchers.IO)
                .onCompletion { e ->
                    if (e == null) {
                        updateStateOnFetchComplete(
                            pageNumber = pageNumber,
                            isReachEnd = uiState.value.bookmarks.size % size != 0
                        )
                    } else {
                        Log.d("BookmarkListViewModel", "Completed with error")
                        updateFetchingStatus(true)
                    }
                }
                .collectLatest { result ->
                    Log.d("BookmarkListViewModel", "${result}")
                    _uiState.update {
                        it.copy(
                            bookmarks = it.bookmarks.toMutableList().apply {
                                this.add(result)
                            }.distinctBy { e -> e.first.bookmarkId }.toList()
                        )
                    }
                }
        }
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
                isReachEnd = isReachEnd
            )
        }

    fun updateSortOption(option: SortOption) {

    }

}

enum class SortOption { DES_CREATION, ASC_CREATION, DES_REMINDER, ASC_REMINDER }

data class BookmarkListState(
    val bookmarks: List<Pair<BookmarkVO, NoticeVO>> = emptyList(),
    val isRefreshing: Boolean = false,
    val isFetchingCompleted: Boolean = true,
    val sortOption: SortOption = SortOption.ASC_CREATION,
    val pageNumber: Int = 0,
    val isReachEnd: Boolean = false
)