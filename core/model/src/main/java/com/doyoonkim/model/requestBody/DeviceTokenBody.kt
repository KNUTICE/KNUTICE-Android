package com.doyoonkim.model.requestBody

data class DeviceTokenBody(
    val fcmToken: String,
    val deviceType: String = "AOS"
)
