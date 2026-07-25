package com.doyoonkim.model

import kotlinx.serialization.Serializable

@Serializable
data class CarrelRoomStatusVO(
    val id: String = "",
    val name: String = "",
    val total: Int = 0,
    val occupied: Int = 0
)
