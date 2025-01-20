package com.doyoonkim.knutice.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.BookmarkComposableState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val localRepository: NoticeLocalRepository
): ViewModel() {

    // Prevent direct access to the UI STATE from the 'View'
    private var _uiState = MutableStateFlow(BookmarkComposableState())
    val uiState = _uiState.asStateFlow()

    fun updateBookmarks(bookmarks: List<Bookmark>) {
        _uiState.update {
            it.copy(
                bookmarks = bookmarks
            )
        }
    }

    fun getAllBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.getAllBookmarks()
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            updateBookmarks(it)
                        },
                        onFailure = {
                            Log.d("BookmarkViewModel", "Failed to fetch bookmarks")
                        }
                    )
                }
        }
    }

}

