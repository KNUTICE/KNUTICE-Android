package com.doyoonkim.data.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
object SystemCoroutineModule {

    private val applicationScopeCoroutine =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = applicationScopeCoroutine
}
