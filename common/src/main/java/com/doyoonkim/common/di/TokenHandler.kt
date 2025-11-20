package com.doyoonkim.common.di

import com.doyoonkim.model.TokenStatus
import kotlinx.coroutines.flow.Flow

interface TokenHandler {
    suspend operator fun invoke(t: String? = null): TokenStatus

    suspend fun registerNewToken(newToken: String): TokenStatus

    suspend fun updateRegisteredToken(oldToken: String, newToken: String): TokenStatus
}