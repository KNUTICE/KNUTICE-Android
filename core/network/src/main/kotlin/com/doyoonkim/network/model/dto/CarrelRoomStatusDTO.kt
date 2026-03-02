package com.doyoonkim.network.model.dto

import androidx.annotation.Keep
import com.doyoonkim.model.CarrelRoomStatusVO
import com.google.gson.annotations.SerializedName

@Keep
data class CarrelRoomStatusDTO(
    @SerializedName("roomId") var id: String? = null,
    @SerializedName("roomName") var name: String? = null,
    @SerializedName("totalSeat") var total: Int? = null,
    @SerializedName("availableSeat") var available: Int? = null,
    @SerializedName("occupiedSeat") var occupied: Int? = null,
    @SerializedName("rowCount") var rowCount: Int? = null,
    @SerializedName("columnCount") var columnCount: Int? = null
) {
    fun toVO() =
        CarrelRoomStatusVO(
            name = this.name ?: "",
            total = this.total ?: 0,
            occupied = this.occupied ?: 0
        )
}