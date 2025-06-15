package com.doyoonkim.model

data class TopicSubscriptionPreferencesVO(
    val general: Boolean = false,
    val scholarship: Boolean = false,
    val event: Boolean = false,
    val academic: Boolean = false,
    val employment: Boolean = false
)
