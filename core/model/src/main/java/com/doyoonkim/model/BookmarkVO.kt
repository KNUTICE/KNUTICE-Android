package com.doyoonkim.model

data class BookmarkVO(
    val bookmarkId: Int = 0,
    // nttId is being used as ID for local Push Notification as well.
    val targetNoticeNttId: Int,
    val isScheduled: Boolean,
    val reminderSchedule: Long,
    val bookmarkNote: String,
    val createdAt: Long
)
