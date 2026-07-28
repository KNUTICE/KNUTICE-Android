package com.doyoonkim.model

data class TopicSubscriptionStatusVO(
    val type: TopicType = TopicType.NOTICE,
    val subscribed: ArrayList<String> = ArrayList<String>()
)

data class NoticeTopicSubscriptionVO(
    val general: Boolean = false,
    val scholarship: Boolean = false,
    val event: Boolean = false,
    val academic: Boolean = false,
    val employment: Boolean = false
)
