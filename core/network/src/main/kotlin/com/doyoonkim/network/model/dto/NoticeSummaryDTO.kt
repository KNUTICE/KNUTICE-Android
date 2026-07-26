package com.doyoonkim.network.model.dto

import com.google.gson.annotations.SerializedName

data class NoticeSummaryDTO(
    @SerializedName("nttId") val nttId: Int,
    @SerializedName("contentSummary") val rawSummary: String
)
