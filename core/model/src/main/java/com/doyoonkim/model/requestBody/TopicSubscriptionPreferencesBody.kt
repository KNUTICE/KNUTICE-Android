package com.doyoonkim.model.requestBody

import com.doyoonkim.model.TopicType

data class TopicSubscriptionPreferencesBody(
    val topicType: TopicType = TopicType.NOTICE,
    val noticeName: String = "Unspecified",
    val isSubscribed: Boolean = false
)
