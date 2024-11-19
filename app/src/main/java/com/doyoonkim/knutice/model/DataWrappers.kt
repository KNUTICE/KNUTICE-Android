package com.doyoonkim.knutice.model

import com.google.android.gms.common.internal.Objects
import com.google.gson.annotations.SerializedName


data class Result(
    @SerializedName("resultCode") var resultCode: Int? = null,
    @SerializedName("resultMessage") var resultMessage: String? = null,
    @SerializedName("resultDescription") var resultDescription: String? = null
)

data class RawNoticeData(
    @SerializedName("nttId") var nttId: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("contentUrl") var contentUrl: String? = null,
    @SerializedName("contentImage") var contentImage: String? = null,
    @SerializedName("departName") var departName: String? = null,
    @SerializedName("registeredAt") var registeredAt: String? = null
)

// POJO for receiving raw data from the server.
data class TopThreeNotices(
    @SerializedName("result" ) var result : Result? = Result(),
    @SerializedName("body"   ) var body   : Body?   = Body()
) {
    data class Body(
        @SerializedName("latestThreeGeneralNews") var latestThreeGeneralNews: ArrayList<RawNoticeData> = arrayListOf(),
        @SerializedName("latestThreeScholarshipNews") var latestThreeScholarshipNews: ArrayList<RawNoticeData> = arrayListOf(),
        @SerializedName("latestThreeEventNews") var latestThreeEventNews: ArrayList<RawNoticeData> = arrayListOf(),
        @SerializedName("latestThreeAcademicNews") var latestThreeAcademicNews: ArrayList<RawNoticeData> = arrayListOf()
    )
}

data class NoticesPerPage(
    @SerializedName("result") var result: Result? = Result(),
    @SerializedName("body") var body: ArrayList<RawNoticeData> = arrayListOf()
)

data class ValidateTokenResult(
    @SerializedName("result") var result: Result? = Result(),
    @SerializedName("body") var body: Body? = Body()
) {
    data class Body(
        val message: String = ""
    )
}

// Data class to be applied to uiState.
data class Notice(
    val nttId: Int = -1,
    val title: String = "Unknown",
    val url: String = "Unknown",
    val imageUrl: String = "",
    val departName: String = "Unknown",
    val timestamp: String = "Unknown"
)

data class ApiDeviceTokenRequest(
    val result: Result = Result(),
    val body: DeviceTokenRequest
)

data class DeviceTokenRequest(
    val deviceToken: String
)

data class DetailedContentState(
    val title: String = "",
    val info: String = "",
    val fullContent: String = "",
    val fullContentUrl: String = "",
    val imageUrl: String = "",
    val isLoaded: Boolean = false
)

data class CustomerServiceReportState(
    val userReport: String = "",
    val reachedMaCharacters: Boolean = false
)