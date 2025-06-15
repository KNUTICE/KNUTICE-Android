package com.doyoonkim.bookmark.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchAllBookmarks
import com.doyoonkim.domain.usecases.FetchAllBookmarksImpl
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkListViewModel @Inject constructor(
    private val fetchAllBookmarks: FetchAllBookmarks
) : ViewModel() {

    private var _uiState = MutableStateFlow(BookmarkListState())
    val uiState = _uiState.asStateFlow()

    fun getAllBookmarks() {
        updateFetchingStatus(false)
        viewModelScope.launch {
            fetchAllBookmarks()
                .flowOn(Dispatchers.IO)
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
        }.run { if (this.isCompleted) updateFetchingStatus(true) }
    }

    fun updateFetchingStatus(status: Boolean) =
        _uiState.update {
            it.copy(
                isFetchingCompleted = status
            )
        }


}

data class BookmarkListState(
    val bookmarks: List<Pair<BookmarkVO, NoticeVO>> = emptyList(),
    val isRefreshing: Boolean = false,
    val isFetchingCompleted: Boolean = true,
)