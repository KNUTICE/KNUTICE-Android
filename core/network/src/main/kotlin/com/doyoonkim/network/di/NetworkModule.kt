package com.doyoonkim.network.di

import com.doyoonkim.network.BuildConfig
import com.doyoonkim.network.retrofit.KnuticeService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    fun providesKnuticeApi(provider: RetrofitProvider): KnuticeService {
        return provider.provide().create(KnuticeService::class.java)
    }

}