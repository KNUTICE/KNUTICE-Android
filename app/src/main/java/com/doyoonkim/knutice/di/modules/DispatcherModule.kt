package com.doyoonkim.knutice.di.modules

import com.doyoonkim.model.di.DefaultDispatcher
import com.doyoonkim.model.di.IoDispatcher
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
object DispatcherModule {

    @Provides
    @Singleton
    @IoDispatcher
    fun providesIoDispatchers(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @DefaultDispatcher
    fun providesDefaultDispatchers(): CoroutineDispatcher = Dispatchers.Default

}