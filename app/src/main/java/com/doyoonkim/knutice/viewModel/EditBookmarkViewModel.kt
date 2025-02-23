package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.EditBookmarkState
import com.doyoonkim.knutice.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBookmarkViewModel @Inject constructor(
    private val localRepository: NoticeLocalRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _uiState = MutableStateFlow(EditBookmarkState())
    val uiState: StateFlow<EditBookmarkState> = _uiState.asStateFlow()

    private val requestNotice = savedStateHandle.toRoute<Notice>()

    init {
        _uiState.update {
            it.copy(
                targetNotice = requestNotice
            )
        }
        getBookmarkByNotice()
    }

    fun updateBookmarkNotes(newString: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (uiState.value.bookmarkNote.length < 500) {
                _uiState.update {
                    it.copy(
                        bookmarkNote = newString
                    )
                }
            }
        }
    }

    private fun getBookmarkByNotice() {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.getBookmarkByNttID(requestNotice.nttId)
                .fold(
                    onSuccess = { bookmark ->
                        if (bookmark.bookmarkId != -1) {
                            _uiState.update {
                                it.copy(
                                    bookmarkId = bookmark.bookmarkId ?: 0,
                                    isReminderRequested = bookmark.isScheduled ?: false,
                                    timeForRemind = bookmark.reminderSchedule ?: 0,
                                    bookmarkNote = bookmark.note ?: "",
                                    requireCreation = false
                                )
                            }
                        }
                    },
                    onFailure = {
                        Log.d("EditBookmarkViewModel", "There is no existing bookmark for this notice.")
                    }
                )
        }
    }

    fun createNewBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            val targetNotice = uiState.value.targetNotice
            val newBookmark = Bookmark(
                bookmarkId = 0,
                isScheduled = uiState.value.isReminderRequested,
                reminderSchedule = 0,
                note = uiState.value.bookmarkNote,
                nttId = targetNotice.nttId
            )

            localRepository.createBookmark(newBookmark, targetNotice).fold(
                onSuccess = {
                    Log.d("EditBookmarkViewModel", "Creation Inquiry Processed Successfully with state: $it")
                },
                onFailure = {
                    Log.d("EditBookmarkViewModel", "Unable to process creation inquiry\nREASON:${it.message}")
                }
            )
        }
    }

    fun modifyBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            val targetNotice = uiState.value.targetNotice
            val modifiedBookmark = Bookmark(
                bookmarkId = uiState.value.bookmarkId,
                isScheduled = uiState.value.isReminderRequested,
                reminderSchedule = uiState.value.timeForRemind,
                note = uiState.value.bookmarkNote,
                nttId = uiState.value.targetNotice.nttId
            )

            localRepository.updateBookmark(modifiedBookmark)
        }
    }

    fun removeBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            val targetBookmark = Bookmark(
                bookmarkId = uiState.value.bookmarkId,
                isScheduled = uiState.value.isReminderRequested,
                reminderSchedule = uiState.value.timeForRemind,
                note = uiState.value.bookmarkNote,
                nttId = uiState.value.targetNotice.nttId
            )

            // The code snippet below should be isolated into separated class under the domain layer.
            // Remove related notice entity first.
            localRepository.deleteNoticeEntity(uiState.value.targetNotice.toNoticeEntity())
            // Once notice entity is being deleted, target bookmark would be deleted.
            localRepository.deleteBookmark(targetBookmark)
        }
    }

}