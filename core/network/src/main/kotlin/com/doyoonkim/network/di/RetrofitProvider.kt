package com.doyoonkim.network.di

import com.doyoonkim.network.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RetrofitProvider @Inject constructor() {

    fun provide(timeout: Long = 10): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_LIVE)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        // User-Agent Interceptor
                        val req = chain.request()
                            .newBuilder()
                            .header("User-Agent", "android")
                            .build()
                        chain.proceed(req)
                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}