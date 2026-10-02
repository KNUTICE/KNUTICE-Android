package com.doyoonkim.network.model.dto

import com.google.gson.annotations.SerializedName

data class TopicSubscriptionStatus(
    @SerializedName("subscribedTopics") val subscribed: List<SubscribedTopic>
)

data class SubscribedTopic(
    val topic: String,
    @SerializedName("topicId") val id: Int,
    val name: String,
    val college: String
)
