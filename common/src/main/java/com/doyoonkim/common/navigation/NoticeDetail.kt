package com.doyoonkim.common.navigation

import kotlinx.serialization.Serializable

@Serializable
data class NoticeDetail(
    val nttId: Int,
    val contentUrl: String,
    val isFabVisible: Boolean = true
)
