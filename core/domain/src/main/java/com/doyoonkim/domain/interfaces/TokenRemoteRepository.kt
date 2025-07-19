package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.requestBody.DeviceTokenBody
import kotlinx.coroutines.flow.Flow

interface TokenRemoteRepository {
    fun requestTokenValidation(body: DeviceTokenBody): Flow<Boolean>

    fun requestUpdateValidatedToken(fcmToken: String)
}