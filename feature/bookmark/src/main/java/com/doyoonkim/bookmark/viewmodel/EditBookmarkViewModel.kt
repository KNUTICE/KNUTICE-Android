package com.doyoonkim.bookmark.viewmodel

import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.domain.usecases.FetchNoticeByIdFromLocal
import com.doyoonkim.domain.usecases.ModifyBookmark
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.notification.local.NotificationAlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditBookmarkViewModel @Inject constructor(
    private val modifyBookmark: ModifyBookmark,
    private val fetchNoticeByIdLocal: FetchNoticeByIdFromLocal,
    private val alarmScheduler: NotificationAlarmScheduler
) : ViewModel() {

    private var _uiState = MutableStateFlow(EditBookmarkState())
    val uiState = _uiState.asStateFlow()

    private var bookmarkNav: BookmarkInfo? = null

    fun getBookmarkByNoticeId(nttId: Int) =
        viewModelScope.launch {
            modifyBookmark.query(nttId)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    _uiState.update {
                        it.copy(
                            bookmarkId = result.bookmarkId,
                            isReminderRequested = result.isScheduled,
                            timeForRemind = result.reminderSchedule,
                            bookmarkNote = result.bookmarkNote,
                            requireCreation = false,
                            bookmarkInstances = result
                        )
                    }
                }
        }

    fun getNoticeById(nttId: Int) {
        _uiState.update {
            it.copy(
                targetNoticeId = nttId
            )
        }

        viewModelScope.launch {
            fetchNoticeByIdLocal(nttId)
                .flowOn(Dispatchers.IO)
                .collectLatest { notice ->
                    _uiState.update {
                        it.copy(
                            targetNotice = notice
                        )
                    }
                }
        }
    }

    fun createBookmarkInfo(info: BookmarkInfo) {
        bookmarkNav = info
    }

    fun updateCompletionStatus(status: Boolean) =
        _uiState.update {
            it.copy(
                isCompleted = status
            )
        }

    fun updateReminderOptions(
        requested: Boolean = uiState.value.isReminderRequested,
        timeForRemind: Long = uiState.value.timeForRemind,
        isDatePickerVisible: Boolean = uiState.value.datePickerVisible
    ) {
        _uiState.update {
            it.copy(
                isReminderRequested = requested,
                timeForRemind = timeForRemind,
                datePickerVisible = isDatePickerVisible
            )
        }
    }

    fun updateBookmarkNotes(notes: String) {
        if (notes.length < 500) {
            _uiState.update {
                it.copy(
                    bookmarkNote = notes
                )
            }
        }
    }

    @RequiresPermission("android.permission.SCHEDULE_EXACT_ALARM")
    fun submitBookmark() =
        viewModelScope.launch {
            // Bookmark creation requires getting Notice Instance.
            val bookmark = uiState.value.run {
                if (this.targetNotice != null) {
                    // Update
                    BookmarkVO(
                        bookmarkId = bookmarkId,
                        targetNoticeNttId = targetNoticeId,
                        isScheduled = isReminderRequested,
                        reminderSchedule = timeForRemind,
                        bookmarkNote = bookmarkNote
                    )
                } else {
                    // Creation
                    BookmarkVO(
                        targetNoticeNttId = targetNoticeId,
                        isScheduled = isReminderRequested,
                        reminderSchedule = timeForRemind,
                        bookmarkNote = bookmarkNote
                    )
                }
            }

            modifyBookmark.createOrUpdate(bookmark, uiState.value.targetNotice)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    if (result) {
                        // Access AlarmScheduler to set local alarm
                        bookmarkNav?.let {
                            if (bookmark.isScheduled) alarmScheduler.schedule(bookmark, it)
                            else alarmScheduler.cancel(bookmark, it)
                        }
                    }
                    _uiState.update {
                        it.copy(
                            isSuccessful = result,
                            isCompleted = true
                        )
                    }
                }
        }

    @RequiresPermission("android.permission.SCHEDULE_EXACT_ALARM")
    fun removeBookmark() {
        viewModelScope.launch {
            val bookmark = uiState.value.run {
                BookmarkVO(
                    bookmarkId = bookmarkId,
                    targetNoticeNttId = targetNoticeId,
                    isScheduled = isReminderRequested,
                    reminderSchedule = timeForRemind,
                    bookmarkNote = bookmarkNote
                )
            }

            if (uiState.value.targetNotice == null) {
                _uiState.update {
                    it.copy(
                        isSuccessful = false,
                        isCompleted = true
                    )
                }
            } else {
                modifyBookmark.delete(bookmark, uiState.value.targetNotice!!)
                    .flowOn(Dispatchers.IO)
                    .collectLatest { result ->
                        if (result) {
                            // Access AlarmScheduler to set local alarm
                            bookmarkNav?.let {
                                if (bookmark.isScheduled) alarmScheduler.schedule(bookmark, it)
                                else alarmScheduler.cancel(bookmark, it)
                            }
                        }
                        _uiState.update {
                            it.copy(
                                requireCreation = true,
                                isSuccessful = true,
                                isCompleted = true
                            )
                        }
                    }
            }
        }
    }

}

data class EditBookmarkState(
    val bookmarkId: Int = 0,
    val targetNoticeId: Int = 0,
    val isReminderRequested: Boolean = false,
    val timeForRemind: Long = 0,
    val bookmarkNote: String = "",
    val requireCreation: Boolean = true,
    val bookmarkInstances: BookmarkVO? = null,
    val targetNotice: NoticeVO? = null,
    val datePickerVisible: Boolean = false,
    val isSuccessful: Boolean = false,
    val isCompleted: Boolean = false
)