package com.doyoonkim.model

data class BookmarkFtsVO(
    val ftsId: Int,
    val bookmarkNote: String,
    val noticeTitle: String,
    val bookmarkNoteTokenized: String,
    val noticeTitleTokenized: String
)
