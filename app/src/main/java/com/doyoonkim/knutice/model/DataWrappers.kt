package com.doyoonkim.knutice.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


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
@Serializable
data class Notice(
    val nttId: Int = -1,
    val title: String = "Unknown",
    val url: String = "Unknown",
    val imageUrl: String = "",
    val departName: String = "Unknown",
    val timestamp: String = "Unknown"
) {
    fun toFullContent(): FullContent {
        return FullContent(
            title,
            "[$departName] $timestamp",
            url,
            imageUrl
        )
    }

    fun toNoticeEntity(): NoticeEntity {
        return NoticeEntity(
            noticeEntityId = 0,
            nttId = nttId,
            title = title,
            url = url,
            imageUrl = imageUrl,
            departName = departName,
            timestamp = timestamp
        )
    }
}

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
    val exceedMinCharacters: Boolean = false,
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
    val bookmarks: List<Pair<Bookmark, Notice>> = emptyList(),
    val isRefreshing: Boolean = false,
    val isRefreshRequested: Boolean = false
)

// EditBookmark
data class EditBookmarkState(
    val bookmarkId: Int = 0,
    val targetNotice: Notice = Notice(),
    val isReminderRequested: Boolean = false,
    val timeForRemind: Long = 0,      // Should be replaced with an appropriate data type later.
    val bookmarkNote: String = "",
    val requireCreation: Boolean = true
)

