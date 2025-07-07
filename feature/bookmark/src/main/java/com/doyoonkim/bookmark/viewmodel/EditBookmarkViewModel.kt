package com.doyoonkim.bookmark.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.domain.usecases.FetchNoticeByIdFromLocal
import com.doyoonkim.domain.usecases.ModifyBookmark
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.notification.local.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Calendar
import javax.inject.Inject

class EditBookmarkViewModel @Inject constructor(
    private val modifyBookmark: ModifyBookmark,
    private val fetchNoticeByIdLocal: FetchNoticeByIdFromLocal,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private var _uiState = MutableStateFlow(EditBookmarkState())
    val uiState = _uiState.asStateFlow()

    private var bookmarkNav: BookmarkInfo? = null
    private val calendar = Calendar.getInstance()

    // Functions should be called during initialization
    init {
        // By following normal user interaction, Alarm Permission should be granted at this point.
        if (!alarmScheduler.canScheduleExactAlarms())
            _uiState.update {
                it.copy(
                    alarmPermissionStatus = false
                )
            }
    }

    fun getBookmarkByNoticeId(nttId: Int) =
        viewModelScope.launch {
            withTimeout(3000L) {
                modifyBookmark.query(nttId)
                    .flowOn(Dispatchers.IO)
                    .collectLatest { result ->
                        _uiState.update {
                            it.copy(
                                bookmarkId = result.bookmarkId,
                                isReminderRequested = result.isScheduled,
                                timeForRemind = result.reminderSchedule,
                                bookmarkNote = result.bookmarkNote,
                                createdAt = result.createdAt,
                                updatedAt = result.updatedAt,
                                requireCreation = false,
                                bookmarkInstances = result
                            )
                        }.also {
                            calendar.timeInMillis = result.reminderSchedule
                        }
                    }
            }.runCatching {
                /* DO NOTHING (PROCESS COMPLETED ON TIME) */
            }.onFailure {
                _uiState.update {
                    it.copy(
                        requireCreation = true
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
            withTimeout(2000L) {
                fetchNoticeByIdLocal(nttId)
                    .flowOn(Dispatchers.IO)
                    .collectLatest { notice ->
                        _uiState.update {
                            it.copy(
                                targetNotice = notice,
                                requireCreation = false
                            )
                        }
                    }
            }.runCatching {
                /* DO NOTHING (PROCESS COMPLETED ON TIME) */
            }.onFailure {
                _uiState.update {
                    it.copy(
                        requireCreation = true
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
    ) {
        _uiState.update {
            it.copy(
                isReminderRequested = requested
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

    fun updateDateInfo(
        year: Int,
        month: Int,
        date: Int
    ) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DATE, date)
    }

    fun updateTimeInfo(
        hour: Int,
        minute: Int
    ) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
    }



    @SuppressLint("android.permission.SCHEDULE_EXACT_ALARM")
    fun submitBookmark() =
        viewModelScope.launch {
            // Bookmark creation requires getting Notice Instance.
            val bookmark = uiState.value.run {
                if (this.requireCreation) {
                    // Creation
                    BookmarkVO(
                        targetNoticeNttId = targetNoticeId,
                        isScheduled = isReminderRequested,
                        reminderSchedule = calendar.timeInMillis,
                        bookmarkNote = bookmarkNote,
                        createdAt = createdAt,
                        updatedAt = createdAt

                    )
                } else {
                    // Update
                    BookmarkVO(
                        bookmarkId = bookmarkId,
                        targetNoticeNttId = targetNoticeId,
                        isScheduled = isReminderRequested,
                        reminderSchedule = calendar.timeInMillis,
                        bookmarkNote = bookmarkNote,
                        createdAt = createdAt,
                        updatedAt = System.currentTimeMillis()
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

    @SuppressLint("android.permission.SCHEDULE_EXACT_ALARM")
    fun removeBookmark() {
        viewModelScope.launch {
            val bookmark = uiState.value.run {
                BookmarkVO(
                    bookmarkId = bookmarkId,
                    targetNoticeNttId = targetNoticeId,
                    isScheduled = isReminderRequested,
                    reminderSchedule = timeForRemind ?: calendar.timeInMillis,
                    bookmarkNote = bookmarkNote,
                    createdAt = createdAt,
                    updatedAt = updatedAt
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
                                // Cancel Alarm Anyways
                                alarmScheduler.cancel(bookmark, it)
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
    val timeForRemind: Long? = null,
    val bookmarkNote: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = 0,
    val requireCreation: Boolean = true,
    val bookmarkInstances: BookmarkVO? = null,
    val targetNotice: NoticeVO? = null,
    val datePickerVisible: Boolean = false,
    val timePickerVisible: Boolean = false,
    val isSuccessful: Boolean = false,
    val isCompleted: Boolean = false,
    val alarmPermissionStatus: Boolean = true
)