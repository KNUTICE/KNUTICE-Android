package com.doyoonkim.network.di

import com.doyoonkim.network.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

class RetrofitProvider @Inject constructor() {

    fun provide(timeout: Long = 10): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_LIVE)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}