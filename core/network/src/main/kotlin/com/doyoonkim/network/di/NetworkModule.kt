package com.doyoonkim.network.di

import com.doyoonkim.network.BuildConfig
import com.doyoonkim.network.DeviceToken
import com.doyoonkim.network.retrofit.KnuticeService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [DeviceTokenModule::class])
object NetworkModule {

    @Provides
    fun providesKnuticeApi(provider: RetrofitProvider): KnuticeService {
        return provider.provide().create(KnuticeService::class.java)
    }
}

@Module(includes = [DeviceTokenModule::class])
object NotificationNetworkModule {
    @Provides
    fun providesKnuticeApi(provider: RetrofitProvider): KnuticeService {
        return provider.provide(5).create(KnuticeService::class.java)
    }
}

@Module
object DeviceTokenModule {
    // Consider to annotated as @Binds
    @Provides
    fun providesDeviceTokenInstance(): DeviceToken = DeviceToken
}