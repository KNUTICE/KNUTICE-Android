package com.doyoonkim.model

import kotlinx.serialization.Serializable

@Serializable
data class NoticeVO(
    val nttId: Int = -1,
    val title: String = "",
    val url: String = "",
    val imageUrl: String? = null,
    val info: String = ""
)