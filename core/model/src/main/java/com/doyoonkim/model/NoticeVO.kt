package com.doyoonkim.model

import kotlinx.serialization.Serializable

//@Serializable
data class NoticeVO(
    val entityId: Int? = null,      // Later migrated and removed.
    val nttId: Int = -1,
    val title: String = "",
    val url: String = "",
    val imageUrl: String? = null,
    val departName: String = "",
    val timestamp: String = ""
)