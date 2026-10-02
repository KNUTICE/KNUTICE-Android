package com.doyoonkim.network.retrofit

import com.doyoonkim.network.model.TopicSubscriptionUpdateRequest
import model.TopicSubscriptionStatusResponse
import model.TopicSubscriptionUpdateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Query

private const val version = "open-api/v2/"
private const val service = "topics"
private const val path = version + service

interface KnuticeTopicService {

    @GET(path)
    suspend fun getAvailableTopic(
        @Header("fcmToken") token: String,
        @Query("type") type: String
    ): TopicSubscriptionStatusResponse

    @PATCH(path)
    suspend fun updateTopicSubscription(
        @Header("fcmToken") token: String,
        @Query("type") type: String,
        @Body request: TopicSubscriptionUpdateRequest
    ): TopicSubscriptionUpdateResponse
}
