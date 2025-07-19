package com.doyoonkim.data.model

/* INTERMEDIATE DATA CLASS FOR QUERY RESULT */

data class BookmarkAsListElement(
    val bookmarkId: Int,
    val noticeId: Int,
    val noticeTitle: String,
    val noticeCategory: String,
    val isReminderSet: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {

}