package com.doyoonkim.network.di

import com.doyoonkim.network.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitProvider @Inject constructor() {

    fun provide(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_LIVE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}