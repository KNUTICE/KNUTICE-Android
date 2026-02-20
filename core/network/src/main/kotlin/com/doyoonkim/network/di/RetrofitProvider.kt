package com.doyoonkim.network.di

import com.doyoonkim.network.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {
    private val baseOkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // User-Agent Interceptor
                val req = chain.request()
                    .newBuilder()
                    .header("User-Agent", "android")
                    .build()
                chain.proceed(req)
            }.build()
    }

    // Default API
    internal val defaultRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_LIVE)
            .client(
                // use .newBuilder() call to share ConnectionPool with baseOkHttpClient
                baseOkHttpClient.newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}