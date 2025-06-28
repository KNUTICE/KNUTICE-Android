package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.requestBody.DeviceTokenBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


interface ValidateDeviceToken {
    operator fun invoke(requestBody: DeviceTokenBody): Flow<Boolean>
}

class ValidateDeviceTokenImpl @Inject constructor(
    private val remoteRepository: RemoteRepository
) : ValidateDeviceToken {

    override operator fun invoke(requestBody: DeviceTokenBody) =
        remoteRepository.requestTokenValidation(requestBody)
            .catch {
                /* Internal Error. Consume values, and never emit values. */
                emit(false)
            }.flowOn(Dispatchers.IO)
}