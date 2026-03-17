package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.CarrelRoomStatusVO


interface CarrelStatusRemoteRepository {

    suspend fun getCarrelRoomStatus(): Result<List<CarrelRoomStatusVO>>

}