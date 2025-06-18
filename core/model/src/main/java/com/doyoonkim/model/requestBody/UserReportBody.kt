package com.doyoonkim.model.requestBody

data class UserReportBody(
    val fcmToken: String? = null,
    val content: String = "",
    val clientType: String = "APP",
    val deviceName: String = "Android Device",
    val version: String = "Unspecified"
)
