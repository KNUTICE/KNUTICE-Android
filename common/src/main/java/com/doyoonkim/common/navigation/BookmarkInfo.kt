package com.doyoonkim.common.navigation

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkInfo(
    val noticeId: Int,
    val noticeTitle: String,
    val noticeInfo: String
)
