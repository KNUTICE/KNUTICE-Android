package com.doyoonkim.network.di

import com.doyoonkim.network.DeviceToken
import com.doyoonkim.network.retrofit.KnuticeService
import dagger.Module
import dagger.Provides

@Module(includes = [DeviceTokenModule::class])
object NetworkModule {
    @Provides
    fun providesKnuticeApi(): KnuticeService {
        return RetrofitProvider.defaultRetrofit.create(KnuticeService::class.java)
    }
}

@Module
object DeviceTokenModule {
    // Consider to annotated as @Binds
    @Provides
    fun providesDeviceTokenInstance(): DeviceToken = DeviceToken
}