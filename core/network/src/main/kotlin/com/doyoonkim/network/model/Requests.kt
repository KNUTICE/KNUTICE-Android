package com.doyoonkim.network.model

import model.NetworkResult

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:10 AM
 */
data class DeviceTokenRequest(
    val result: NetworkResult? = NetworkResult(),
    val body: DeviceTokenRequestBody
) {
    data class DeviceTokenRequestBody(
        val fcmToken: String
    )
}

data class UserReportRequest(
    val result: NetworkResult? = NetworkResult(),
    val body: UserReportRequestBody
) {
    data class UserReportRequestBody(
        val fcmToken: String,
        val content: String = "",
        val clientType: String = "APP",
        val deviceName: String = "",
        val version: String = ""
    )
}

data class TopicSubscriptionPreferencesRequest(
    val result: NetworkResult? = NetworkResult(),
    val body: TopicSubscriptionPreferencesRequestBody
) {
    data class TopicSubscriptionPreferencesRequestBody(
        val fcmToken: String,
        val noticeName: String = "",
        val isSubscribed: Boolean = false
    )
}