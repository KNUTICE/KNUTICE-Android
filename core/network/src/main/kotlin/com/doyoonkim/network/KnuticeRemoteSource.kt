package com.doyoonkim.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:27 AM
 */

@Singleton
class KnuticeRemoteSource @Inject constructor() {
    private val TAG = "KnuticeRemoteSource"

    private val knuticeServie = Retrofit.Builder()
        .baseUrl("")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}