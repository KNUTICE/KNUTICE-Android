package com.doyoonkim.network


object DeviceToken {
    private var validatedToken = ""

    fun updateValidatedToken(token: String) {
        validatedToken = token
    }

    fun validatedToken(): String = validatedToken
}