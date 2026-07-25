package com.doyoonkim.network.model

import androidx.annotation.Keep

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:10 AM
 */

@Keep
data class FcmTokenSaveRequest(
    val deviceType: String
)

@Keep
data class FcmTokenUpdateRequest(
    val oldFcmToken: String,
    val deviceType: String
)

@Keep
data class ReportSaveRequest(
    val content: String = "",
    val deviceName: String = "",
    val version: String = ""
)

@Keep
data class TopicUpdateRequest(
    val topic: String,
    val enabled: Boolean
)
