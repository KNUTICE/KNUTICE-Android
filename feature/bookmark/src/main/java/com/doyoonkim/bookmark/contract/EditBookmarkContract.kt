package com.doyoonkim.bookmark.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.model.BookmarkVO
import com.doyoonkim.model.NoticeVO

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
    val isSuccessful: Boolean = false,
    val isCompleted: Boolean = false,
    val alarmPermissionStatus: Boolean = true
) : UiState

sealed class EditBookmarkEvent : UiEvent {
    data object CheckAlarmPermissionState: EditBookmarkEvent()
    data class GetBookmarkInformation(val info: BookmarkInfo): EditBookmarkEvent()
    data object UpdateReminderOption: EditBookmarkEvent()
    data class UpdateReminderDate(val year: Int, val month: Int, val day: Int): EditBookmarkEvent()
    data class UpdateReminderTime(val hour: Int, val min: Int): EditBookmarkEvent()
    data class UpdateBookmarkNotes(val notes: String): EditBookmarkEvent()
    data object RequestNoticeDetail: EditBookmarkEvent()
    data object SaveBookmark: EditBookmarkEvent()
    data object RemoveBookmark: EditBookmarkEvent()
    data object ValidateProcessResult: EditBookmarkEvent()
    data object GoBack: EditBookmarkEvent()
}

sealed class EditBookmarkSideEffect : UiSideEffect {
    data class NavToSelectedNotice(val dest: NoticeDetail): EditBookmarkSideEffect()
    data object NavToBack: EditBookmarkSideEffect()
    data object ExitOnCompletion: EditBookmarkSideEffect()
}