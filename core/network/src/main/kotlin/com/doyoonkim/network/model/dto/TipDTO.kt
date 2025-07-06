package com.doyoonkim.network.model.dto

import com.doyoonkim.model.TipVO

data class TipDTO(
    val id: String,
    val title: String,
    val url: String,
    val deviceType: String,
    val registeredAt: String
) {
    fun toVO() =
        TipVO(
            id = id,
            title = title,
            url = url,
            registeredAt = registeredAt
        )
}
