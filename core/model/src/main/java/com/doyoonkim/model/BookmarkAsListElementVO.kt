package com.doyoonkim.model

data class BookmarkAsListElementVO(
    val bookmarkId: Int,
    val noticeId: Int,
    val noticeTitle: String,
    val noticeCategory: String,
    val isReminderSet: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
