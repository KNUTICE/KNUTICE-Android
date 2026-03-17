package com.doyoonkim.network.di

import com.doyoonkim.network.BuildConfig
import com.doyoonkim.network.retrofit.KnuticeService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkModule {
    @Provides
    @Singleton
    fun providesKnuticeApi(): KnuticeService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                // User-Agent Interceptor
                val req = chain.request()
                    .newBuilder()
                    .header("User-Agent", "android")
                    .build()
                chain.proceed(req)
            }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_LIVE)
            .client(
                // use .newBuilder() call to share ConnectionPool with baseOkHttpClient
                okHttpClient.newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(KnuticeService::class.java)
    }
}