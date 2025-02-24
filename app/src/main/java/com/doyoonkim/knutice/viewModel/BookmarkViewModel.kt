package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.domain.FetchBookmarkFromDatabase
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.BookmarkComposableState
import com.doyoonkim.knutice.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val localRepository: NoticeLocalRepository,
    private val fetchBookmarkFromDatabase: FetchBookmarkFromDatabase
): ViewModel() {

    // Prevent direct access to the UI STATE from the 'View'
    private var _uiState = MutableStateFlow(BookmarkComposableState())
    val uiState = _uiState.asStateFlow()

    fun updateBookmarks(newPair: Pair<Bookmark, Notice>) {
        _uiState.update {
            it.copy(
                bookmarks = uiState.value.bookmarks.toMutableList().apply {
                    this.add(newPair)
                }.distinctBy { e -> e.first.bookmarkId }.toList()
            )
        }

        Log.d("BookmarkViewModel", "Updated Bookmark Status: ${uiState.value.bookmarks}")
    }

    fun getAllBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchBookmarkFromDatabase.getAllBookmarkWithNotices()
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            Log.d("BookmarkViewModel", "Collected: ${it.toString()}")
                            updateBookmarks(it)
                        },
                        onFailure = {
                            Log.d("BookmarkViewModel", "Unable to receive bookmark pair.\nREASON:${it.message}")
                        }
                    )
                }
        }
    }

}

