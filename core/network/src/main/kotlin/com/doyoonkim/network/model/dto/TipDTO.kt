package com.doyoonkim.network.model.dto

import androidx.annotation.Keep
import com.doyoonkim.model.TipVO
import com.google.gson.annotations.SerializedName

@Keep
data class TipDTO(
    @SerializedName("tipId") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("deviceType") val deviceType: String,
    @SerializedName("createdAt") val registeredAt: String
) {
    fun toVO() =
        TipVO(
            id = id,
            title = title,
            url = url,
            registeredAt = registeredAt
        )
}
