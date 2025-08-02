package com.doyoonkim.data.model

import com.doyoonkim.model.BookmarkFtsTargetVO

/* INTERMEDIATE DATA CLASS FOR QUERY RESULT */

data class BookmarkAsListElement(
    val bookmarkId: Int,
    val noticeId: Int,
    val noticeTitle: String,
    val noticeCategory: String,
    val isReminderSet: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

data class BookmarkFtsTarget(
    val bookmarkId: Int,
    val bookmarkNotes: String,
    val noticeTitle: String
) {
    fun toVO() =
        BookmarkFtsTargetVO(
            bookmarkId = this.bookmarkId,
            bookmarkNotes = this.bookmarkNotes,
            noticeTitle = this.noticeTitle
        )
}