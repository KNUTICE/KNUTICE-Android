package com.doyoonkim.infrastructure.firebase

import android.util.Log
import com.doyoonkim.domain.interfaces.abtest.FirebaseRemoteConfigRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await

class FirebaseRemoteConfigRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig
): FirebaseRemoteConfigRepository {
    private val TAG = "FirebaseRemoteConfigRepositoryImpl"

    // Reading value from Heap Memory
    override fun getTestRawVariantValue(key: String): String {
        // If RemoteConfig is not yet ready, it returns default Blank String ("")
        // Default string would be gracefully handled by Domain leve usecase.
        // RemoteConfig instance would be instantiated as app starts, read local cached file and
        // place it in in-memory RAM. (HashMap)
        return remoteConfig.getString(key)
    }

    override suspend fun fetchAndActivate(): Boolean {
        // Call fetchAndActivate for retrieve Remote Config data (Execute per session.
        return try {
            Log.d(TAG, "REMOTE CONFIG FETCH STARTED")
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            Log.d(TAG, "Unable to fetch Remote Config\nReason: ${e.stackTraceToString()}")
            false
        }
    }

}