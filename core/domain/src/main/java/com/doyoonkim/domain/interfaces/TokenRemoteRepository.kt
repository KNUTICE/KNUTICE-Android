package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.TokenStatus
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TokenUpdateBody
import kotlinx.coroutines.flow.Flow

interface TokenRemoteRepository {

    fun requestUpdateValidatedToken(fcmToken: String)

    suspend fun requestUpdateFcmToken(body: TokenUpdateBody): TokenStatus

    suspend fun requestFcmTokenRegistration(body: DeviceTokenBody): TokenStatus
}