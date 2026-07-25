package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TokenRemoteRepository
import com.doyoonkim.model.TokenStatus
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TokenUpdateBody
import javax.inject.Inject

interface ValidateDeviceToken {

    suspend fun register(requestBody: DeviceTokenBody): TokenStatus

    suspend fun update(requestBody: TokenUpdateBody): TokenStatus
}

class ValidateDeviceTokenImpl @Inject constructor(
    private val remoteRepository: TokenRemoteRepository
) : ValidateDeviceToken {

    override suspend fun register(requestBody: DeviceTokenBody) =
        remoteRepository.requestFcmTokenRegistration(requestBody)

    override suspend fun update(requestBody: TokenUpdateBody) =
        remoteRepository.requestUpdateFcmToken(requestBody)
}
