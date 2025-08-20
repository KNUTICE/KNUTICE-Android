package com.doyoonkim.model.requestBody

data class TokenUpdateBody(
    val oldFcmToken: String,
    val newFcmToken: String,
    val deviceType: String = "AOS"
)
