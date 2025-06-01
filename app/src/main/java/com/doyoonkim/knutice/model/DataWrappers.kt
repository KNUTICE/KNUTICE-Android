package com.doyoonkim.knutice.model

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
    @SerializedName("departmentName") var departName: String? = null,
    @SerializedName("registeredAt") var registeredAt: String? = null,
    @SerializedName("noticeName") var noticeCategory: String? = null
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

data class TopicSubscriptionStatusDTO(
    @SerializedName("result") var result: Result? = Result(),
    @SerializedName("body") var body: Body? = Body()
) {
    data class Body(
        @SerializedName("generalNewsTopic") var generalNewsTopic: Boolean = false,
        @SerializedName("scholarshipNewsTopic") var scholarshipNewsTopic: Boolean = false,
        @SerializedName("eventNewsTopic") var eventNewsTopic: Boolean = false,
        @SerializedName("academicNewsTopic") var academicNewsTopic: Boolean = false,
        @SerializedName("employmentNewsTopic") var employmentNewsTopic: Boolean = false
    )
}

data class SingleNotice(
    @SerializedName("result") var result: Result? = Result(),
    @SerializedName("body") var notice: NoticeDTO?
) {
    data class NoticeDTO(
        @SerializedName("nttId") var nttId: Int,
        @SerializedName("title") var title: String,
        @SerializedName("contentUrl") var contentUrl: String,
        @SerializedName("contentImage") var contentImage: String?,
        @SerializedName("departmentName") var departmentName: String,
        @SerializedName("registeredAt") var registeredAt: String,
        @SerializedName("noticeName") var noticeName: String
    ) {
        fun toNotice(): Notice {
            return Notice(
                nttId = nttId,
                title = title,
                url = contentUrl,
                imageUrl = contentImage ?: "",
                departName = departmentName,
                timestamp = registeredAt,
                // noticeCategory = noticeName
            )
        }
    }
}

data class NoticesPerPage(
    @SerializedName("result") var result: Result? = Result(),
    @SerializedName("body") var body: ArrayList<RawNoticeData> = arrayListOf()
)

data class ApiPostResult(
    @SerializedName("result") var result: Result? = Result(),
    @SerializedName("body") var body: Boolean? = null
)

data class ApiDeviceTokenRequest(
    val result: Result = Result(),
    val body: DeviceTokenRequest
)

data class DeviceTokenRequest(
    val fcmToken: String
)

data class ApiReportRequest(
    val result: Result = Result(),
    val body: ReportRequest
)

data class ReportRequest(
    val fcmToken: String = "",
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
    val fcmToken: String = "",
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
            imageUrl,
            nttId.toString()
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
    val requestedNotice: Notice = Notice(),
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
    val isEachChannelAllowed: List<Boolean> = listOf(false, false, false, false),
    val isSyncCompleted: Boolean = false,
    val isError: Boolean = false
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
    val requireCreation: Boolean = true,
    val bookmarkInstance: Bookmark? = null,
    val datePickerVisible: Boolean = false
)

