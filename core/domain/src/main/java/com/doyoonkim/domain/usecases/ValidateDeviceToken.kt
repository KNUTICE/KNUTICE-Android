package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TokenRemoteRepository
import com.doyoonkim.model.TokenStatus
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TokenUpdateBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


interface ValidateDeviceToken {
    operator fun invoke(requestBody: DeviceTokenBody): Flow<Boolean>

    suspend fun register(requestBody: DeviceTokenBody): TokenStatus

    suspend fun update(requestBody: TokenUpdateBody): TokenStatus
}

class ValidateDeviceTokenImpl @Inject constructor(
    private val remoteRepository: TokenRemoteRepository
) : ValidateDeviceToken {

    override operator fun invoke(requestBody: DeviceTokenBody) =
        remoteRepository.requestTokenValidation(requestBody)
            .catch {
                /* Internal Error. Consume values, and never emit values. */
                emit(false)
            }.flowOn(Dispatchers.IO)

    override suspend fun register(requestBody: DeviceTokenBody) =
        remoteRepository.requestFcmTokenRegistration(requestBody)

    override suspend fun update(requestBody: TokenUpdateBody) =
        remoteRepository.requestUpdateFcmToken(requestBody)

}