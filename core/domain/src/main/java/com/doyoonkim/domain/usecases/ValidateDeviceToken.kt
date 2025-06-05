package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.LocalRepository


interface ValidateDeviceToken {
    fun requestTokenValidation(fcmToken: String)
}

class ValidateDeviceTokenImpl constructor(
    private val localRepository: LocalRepository
) : ValidateDeviceToken {
    override fun requestTokenValidation(fcmToken: String) {

    }

    operator fun invoke(fcmToken: String) = requestTokenValidation(fcmToken)
}