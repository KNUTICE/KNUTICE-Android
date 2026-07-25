package com.doyoonkim.bookmark.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import com.doyoonkim.bookmark.contract.EditBookmarkEvent
import com.doyoonkim.bookmark.contract.EditBookmarkMutation
import com.doyoonkim.bookmark.contract.EditBookmarkSideEffect
import com.doyoonkim.bookmark.contract.EditBookmarkState
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.domain.usecases.FetchNoticeByIdFromLocal
import com.doyoonkim.domain.usecases.ModifyBookmark
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.notification.local.AlarmScheduler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Calendar
import javax.inject.Inject

class EditBookmarkViewModel @Inject constructor(
    private val modifyBookmark: ModifyBookmark,
    private val fetchNoticeByIdLocal: FetchNoticeByIdFromLocal,
    private val alarmScheduler: AlarmScheduler
) : BaseViewModel<EditBookmarkState, EditBookmarkEvent, EditBookmarkSideEffect, EditBookmarkMutation>() {
    private val TAG = this.javaClass.name
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
                    mutate(EditBookmarkMutation.AlarmPermissionDenied)
                }
            }
            is EditBookmarkEvent.GetBookmarkInformation -> {
                createBookmarkInfo(event.info)
                mutate(EditBookmarkMutation.Edit.NoticeId(event.info.noticeId))

                getBookmarkByNoticeId(event.info.noticeId)
                getNoticeById(event.info.noticeId)
            }
            is EditBookmarkEvent.UpdateReminderOption -> {
                mutate(EditBookmarkMutation.Edit.Reminder(event.status))
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
                if (uiState.value.isSuccessful) {
                    sendSideEffect(EditBookmarkSideEffect.ExitOnCompletion)
                } else {
                    mutate(EditBookmarkMutation.Edit.Ready) // Set false for completion status for marking a new transaction is ready to start.
                }
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
                            mutate(EditBookmarkMutation.BookmarkFetched(result)).also {
                                calendar.timeInMillis = result.reminderSchedule
                            }
                        }
                }
            }.onFailure {
                mutate(EditBookmarkMutation.CreationNeeded)
            }
        }

    private fun getNoticeById(nttId: Int) {
        viewModelScope.launch {
            withTimeout(2000L) {
                fetchNoticeByIdLocal(nttId)
                    .collectLatest { notice ->
                        mutate(EditBookmarkMutation.NoticeFetched(notice))
                    }
            }.runCatching {
                /* DO NOTHING (PROCESS COMPLETED ON TIME) */
            }.onFailure {
                mutate(EditBookmarkMutation.CreationNeeded)
            }
        }
    }

    private fun createBookmarkInfo(info: BookmarkInfo) {
        bookmarkNav = info
    }

    private fun updateBookmarkNotes(notes: String) {
        if (notes.length < 500) {
            mutate(EditBookmarkMutation.Edit.Notes(notes))
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
            mutate(EditBookmarkMutation.Edit.Processing)

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
                            if (bookmark.isScheduled) {
                                alarmScheduler.schedule(bookmark, it)
                            } else {
                                alarmScheduler.cancel(bookmark, it)
                            }
                        }
                        mutate(EditBookmarkMutation.Edit.Success)
                    } else {
                        mutate(EditBookmarkMutation.Edit.Failure("Unable to create or update."))
                    }
                }
        }

    @SuppressLint("android.permission.SCHEDULE_EXACT_ALARM")
    private fun removeBookmark() {
        viewModelScope.launch {
            mutate(EditBookmarkMutation.Edit.Processing)

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
                mutate(EditBookmarkMutation.Edit.Failure("Related notice not found. Unable to remove."))
            } else {
                modifyBookmark.delete(bookmark, uiState.value.targetNotice!!)
                    .collectLatest { result ->
                        if (result) {
                            // Access AlarmScheduler to set local alarm
                            bookmarkNav?.let {
                                // Cancel Alarm Anyways
                                alarmScheduler.cancel(bookmark, it)
                            }
                            mutate(EditBookmarkMutation.Edit.Success)
                        } else {
                            mutate(EditBookmarkMutation.Edit.Failure("Unable to remove target"))
                        }
                    }
            }
        }.invokeOnCompletion { mutate(EditBookmarkMutation.CreationNeeded) }
    }

    // Main Reducer
    override fun reduce(
        currentState: EditBookmarkState,
        mutation: EditBookmarkMutation
    ): EditBookmarkState {
        return when (mutation) {
            is EditBookmarkMutation.AlarmPermissionDenied -> {
                currentState.copy(alarmPermissionStatus = false)
            }
            is EditBookmarkMutation.CreationNeeded -> {
                currentState.copy(requireCreation = true)
            }
            is EditBookmarkMutation.BookmarkFetched -> {
                with(mutation) {
                    currentState.copy(
                        bookmarkId = bookmark.bookmarkId,
                        isReminderRequested = bookmark.isScheduled,
                        timeForRemind = bookmark.reminderSchedule,
                        bookmarkNote = bookmark.bookmarkNote,
                        createdAt = bookmark.createdAt,
                        updatedAt = bookmark.updatedAt,
                        requireCreation = false
                    )
                }
            }
            is EditBookmarkMutation.NoticeFetched -> {
                currentState.copy(
                    targetNotice = mutation.notice,
                    requireCreation = false
                )
            }
            is EditBookmarkMutation.Edit -> mutation.reducer(currentState)
        }
    }

    // Specialized Reducer
    private fun EditBookmarkMutation.Edit.reducer(state: EditBookmarkState) =
        when (this) {
            is EditBookmarkMutation.Edit.NoticeId -> {
                state.copy(targetNoticeId = nttId)
            }
            is EditBookmarkMutation.Edit.Notes -> {
                state.copy(bookmarkNote = notes)
            }
            is EditBookmarkMutation.Edit.Reminder -> {
                state.copy(isReminderRequested = requested)
            }
            is EditBookmarkMutation.Edit.Ready -> {
                state.copy(isCompleted = false)
            }
            is EditBookmarkMutation.Edit.Processing -> {
                state.copy(isProcessing = true)
            }
            is EditBookmarkMutation.Edit.Success -> {
                state.copy(
                    isProcessing = false,
                    isSuccessful = true,
                    isCompleted = true
                )
            }
            is EditBookmarkMutation.Edit.Failure -> {
                state.copy(
                    isProcessing = false,
                    isSuccessful = false,
                    isCompleted = true
                )
            }
        }
}
