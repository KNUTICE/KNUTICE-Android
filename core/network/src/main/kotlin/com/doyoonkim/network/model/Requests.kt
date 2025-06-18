package com.doyoonkim.network.model

import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import com.doyoonkim.model.requestBody.UserReportBody
import model.NetworkResult

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:10 AM
 */
data class DeviceTokenRequest(
    val result: NetworkResult? = NetworkResult(),
    val body: DeviceTokenBody
)

data class UserReportRequest(
    val result: NetworkResult? = NetworkResult(),
    val body: UserReportBody
)

data class TopicSubscriptionPreferencesRequest(
    val result: NetworkResult? = NetworkResult(),
    val body: TopicSubscriptionPreferencesBody
)