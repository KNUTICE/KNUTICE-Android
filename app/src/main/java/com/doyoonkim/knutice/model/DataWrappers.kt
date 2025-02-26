package com.doyoonkim.knutice.model

import androidx.room.Entity
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

data class ApiPostResult(
    @SerializedName("result") var result: Result? = Result(),
    @SerializedName("body") var body: Body? = Body()
) {
    data class Body(
        val message: String = ""
    )
}

data class ApiDeviceTokenRequest(
    val result: Result = Result(),
    val body: DeviceTokenRequest
)

data class DeviceTokenRequest(
    val deviceToken: String
)

data class ApiReportRequest(
    val result: Result = Result(),
    val body: ReportRequest
)

data class ReportRequest(
    val token: String = "",
    val content: String = "",
    val clientType: String = "APP",
    val deviceName: String = "",
    val version: String = ""
)

data class ApiTopicSubscriptionRequest(
    val result: Result = Result(),
    val body: ManageTopicRequest = ManageTopicRequest()
)

data class ManageTopicRequest(
    val deviceToken: String = "",
    val noticeName: String = "",
    val isSubscribed: Boolean = false
)

// Data class to be applied to uiState.
// Universal
data class Notice(
    val nttId: Int = -1,
    val title: String = "Unknown",
    val url: String = "Unknown",
    val imageUrl: String = "",
    val departName: String = "Unknown",
    val timestamp: String = "Unknown"
)

// DetailedNoticeContent
data class DetailedContentState(
    val url: String = "",
    val title: String = "",
    val info: String = "",
    val fullContent: String = "",
    val fullContentUrl: String = "",
    val imageUrl: String = "",
    val loadingStatue: Float = 0.0f
)

// CustomerService
data class CustomerServiceReportState(
    val userReport: String = "",
    val reachedMaxCharacters: Boolean = false,
    val isSubmissionFailed: Boolean = false,
    val isSubmissionCompleted: Boolean = false
)

// Search
data class SearchNoticeState(
    val searchKeyword: String = "",
    val isQuerying: Boolean = false,
    val queryResult: List<Notice> = emptyList()
)

// NotificationPreference
data class NotificationPreferenceStatus(
    val isMainNotificationPermissionGranted: Boolean = false,
    //TODO: Consider change data type to MAP
    val isEachChannelAllowed: List<Boolean> = listOf(true, true, true, true)
)

// BookmarkComposable
data class BookmarkComposableState(
    val bookmarks: List<Bookmark> = emptyList(),
    val isRefreshing: Boolean = false,
    val isRefreshRequested: Boolean = false
)

