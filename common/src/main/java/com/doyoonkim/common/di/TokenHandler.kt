package com.doyoonkim.common.di

import kotlinx.coroutines.flow.Flow

interface TokenHandler {
    fun handleCurrentTokenRequest(): Flow<Boolean>
}