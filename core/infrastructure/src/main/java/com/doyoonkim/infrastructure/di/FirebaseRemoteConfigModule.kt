package com.doyoonkim.infrastructure.di

import com.doyoonkim.domain.interfaces.abtest.FirebaseRemoteConfigRepository
import com.doyoonkim.infrastructure.BuildConfig
import com.doyoonkim.infrastructure.firebase.FirebaseRemoteConfigRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object FirebaseRemoteConfigModule {

    // FirebaseRemoteConfig
    @Provides
    @Singleton
    fun providesFirebaseRemoteConfigRepository(): FirebaseRemoteConfigRepository {
        val remoteConfig = Firebase.remoteConfig.apply {
            val configurationSettings = remoteConfigSettings {
                // Default Settings: 12 Hours = (43200L)
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0L else 43200L
            }
            setConfigSettingsAsync(configurationSettings)
        }
        return FirebaseRemoteConfigRepositoryImpl(remoteConfig)
    }

}