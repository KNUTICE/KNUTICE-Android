package com.doyoonkim.model

import kotlinx.serialization.Serializable

@Serializable
data class NoticeVO(
    val nttId: Int = -1,
    val title: String = "Unknown",
    val url: String = "Unknown",
    val imageUrl: String = "",
    val info: String = "[Unknown] | 2025-05-15"
)