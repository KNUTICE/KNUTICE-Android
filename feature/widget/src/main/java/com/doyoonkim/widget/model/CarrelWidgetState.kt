package com.doyoonkim.widget.model

import com.doyoonkim.model.CarrelRoomStatusVO
import kotlinx.serialization.Serializable

@Serializable
data class CarrelWidgetState(
    val status: List<CarrelRoomStatusVO> = emptyList()
)
