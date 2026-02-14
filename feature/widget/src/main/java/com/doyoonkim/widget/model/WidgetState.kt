package com.doyoonkim.widget.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetNoticeVO(
    val id: Int,
    val title: String,
    val info: String,
    val contentUrl: String
)

@Serializable
data class WidgetState(
    val category: String = "",
    val notices: List<WidgetNoticeVO> = emptyList(),
    val lastUpdated: Long = 0L
)
