package com.doyoonkim.bookmark.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.bookmark.contract.EditBookmarkEvent
import com.doyoonkim.bookmark.contract.EditBookmarkSideEffect
import com.doyoonkim.bookmark.contract.EditBookmarkState
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.domain.usecases.FetchNoticeByIdFromLocal
import com.doyoonkim.domain.usecases.ModifyBookmark
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.notification.local.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Calendar
import javax.inject.Inject

class EditBookmarkViewModel @Inject constructor(
    private val modifyBookmark: ModifyBookmark,
    private val fetchNoticeByIdLocal: FetchNoticeByIdFromLocal,
    private val alarmScheduler: AlarmScheduler
) : BaseViewModel<EditBookmarkState, EditBookmarkEvent, EditBookmarkSideEffect>() {

    override fun setInitialState(): EditBookmarkState = EditBookmarkState()

    private var bookmarkNav: BookmarkInfo? = null
    private val calendar = Calendar.getInstance()

    // Functions should be called during initialization
    init {
        // By following normal user interaction, Alarm Permission should be granted at this point.
        sendUiEvent(EditBookmarkEvent.CheckAlarmPermissionState)
    }

    override fun handleEvent(event: EditBookmarkEvent) {
        when (event) {
            is EditBookmarkEvent.CheckAlarmPermissionState -> {
                if (!alarmScheduler.canScheduleExactAlarms()) {
                    stateUpdate { it.copy(alarmPermissionStatus = false) }
                }
            }
            is EditBookmarkEvent.GetBookmarkInformation -> {
                createBookmarkInfo(event.info)
                getBookmarkByNoticeId(event.info.noticeId)
                getNoticeById(event.info.noticeId)
            }
            is EditBookmarkEvent.UpdateReminderOption -> {
                updateReminderOptions()
            }
            is EditBookmarkEvent.UpdateReminderDate -> {
                with(event) {
                    updateDateInfo(year, month, day)
                }
            }
            is EditBookmarkEvent.UpdateReminderTime -> {
                with(event) {
                    updateTimeInfo(hour, min)
                }
            }
            is EditBookmarkEvent.UpdateBookmarkNotes -> {
                with(event) {
                    updateBookmarkNotes(notes)
                }
            }
            is EditBookmarkEvent.RequestNoticeDetail -> {
                with(uiState.value) {
                    targetNotice?.let {
                        sendSideEffect(
                            EditBookmarkSideEffect.NavToSelectedNotice(
                                NoticeDetail(
                                    nttId = it.nttId,
                                    contentUrl = it.url,
                                    isFabVisible = false
                                )
                            )
                        )
                    }
                }
            }
            is EditBookmarkEvent.SaveBookmark -> {
                submitBookmark()
            }
            is EditBookmarkEvent.RemoveBookmark -> {
                removeBookmark()
            }
            is EditBookmarkEvent.ValidateProcessResult -> {
                if (uiState.value.isSuccessful) sendSideEffect(EditBookmarkSideEffect.ExitOnCompletion)
                else updateCompletionStatus(false) // Set false for completion status for marking a new transaction is ready to start.
            }
            is EditBookmarkEvent.GoBack -> sendSideEffect(EditBookmarkSideEffect.NavToBack)
        }
    }

    private fun getBookmarkByNoticeId(nttId: Int) =
        viewModelScope.launch {
            runCatching {
                // Querying Bookmark
                withTimeout(3000L) {
                    modifyBookmark.query(nttId)
                        .collectLatest { result ->
                            stateUpdate {
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
                }
            }.onFailure {
                stateUpdate {
                    it.copy(
                        requireCreation = true
                    )
                }
            }
        }

    private fun getNoticeById(nttId: Int) {
        stateUpdate {
            it.copy(
                targetNoticeId = nttId
            )
        }

        viewModelScope.launch {
            withTimeout(2000L) {
                fetchNoticeByIdLocal(nttId)
                    .collectLatest { notice ->
                        stateUpdate {
                            it.copy(
                                targetNotice = notice,
                                requireCreation = false
                            )
                        }
                    }
            }.runCatching {
                /* DO NOTHING (PROCESS COMPLETED ON TIME) */
            }.onFailure {
                stateUpdate {
                    it.copy(
                        requireCreation = true
                    )
                }
            }
        }
    }

    private fun createBookmarkInfo(info: BookmarkInfo) {
        bookmarkNav = info
    }

    private fun updateCompletionStatus(status: Boolean) =
        stateUpdate {
            it.copy(
                isCompleted = status
            )
        }

    private fun updateReminderOptions() {
        stateUpdate {
            it.copy(
                // Set Direct Opposite.
                isReminderRequested = !it.isReminderRequested
            )
        }
    }

    private fun updateBookmarkNotes(notes: String) {
        if (notes.length < 500) {
            stateUpdate {
                it.copy(
                    bookmarkNote = notes
                )
            }
        }
    }

    private fun updateDateInfo(
        year: Int,
        month: Int,
        date: Int
    ) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DATE, date)
    }

    private fun updateTimeInfo(
        hour: Int,
        minute: Int
    ) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
    }



    @SuppressLint("android.permission.SCHEDULE_EXACT_ALARM")
    private fun submitBookmark() =
        viewModelScope.launch {
            stateUpdate {
                it.copy(
                    isProcessing = true
                )
            }
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
                .collectLatest { result ->
                    if (result) {
                        // Access AlarmScheduler to set local alarm
                        bookmarkNav?.let {
                            if (bookmark.isScheduled) alarmScheduler.schedule(bookmark, it)
                            else alarmScheduler.cancel(bookmark, it)
                        }
                    }
                    stateUpdate {
                        it.copy(
                            isProcessing = false,
                            isSuccessful = result,
                            isCompleted = true
                        )
                    }
                }
        }

    @SuppressLint("android.permission.SCHEDULE_EXACT_ALARM")
    private fun removeBookmark() {
        viewModelScope.launch {
            stateUpdate {
                it.copy(
                    isProcessing = true
                )
            }
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
                stateUpdate {
                    it.copy(
                        isSuccessful = false,
                        isCompleted = true
                    )
                }
            } else {
                modifyBookmark.delete(bookmark, uiState.value.targetNotice!!)
                    .collectLatest { result ->
                        if (result) {
                            // Access AlarmScheduler to set local alarm
                            bookmarkNav?.let {
                                // Cancel Alarm Anyways
                                alarmScheduler.cancel(bookmark, it)
                            }
                        }
                        stateUpdate {
                            it.copy(
                                requireCreation = true,
                                isProcessing = false,
                                isSuccessful = result,
                                isCompleted = true
                            )
                        }
                    }
            }
        }
    }

}

data class EditBookmarkState(
    val isUnableToEdit: Boolean = false,
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
    val isProcessing: Boolean = false,
    val isSuccessful: Boolean = false,
    val isCompleted: Boolean = false,
    val alarmPermissionStatus: Boolean = true
)