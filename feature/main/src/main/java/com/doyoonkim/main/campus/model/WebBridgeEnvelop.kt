package com.doyoonkim.main.campus.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class WebBridgeEnvelop(
    val type: String,
    val payload: JsonElement? = null
)

enum class WebAppAction { CLOSE_WEBVIEW }
