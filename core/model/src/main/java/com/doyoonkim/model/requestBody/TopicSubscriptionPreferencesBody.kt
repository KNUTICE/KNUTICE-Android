package com.doyoonkim.model.requestBody

data class TopicSubscriptionPreferencesBody(
    val fcmToken: String? = null,
    val noticeName: String = "Unspecified",
    val isSubscribed: Boolean = false
)
