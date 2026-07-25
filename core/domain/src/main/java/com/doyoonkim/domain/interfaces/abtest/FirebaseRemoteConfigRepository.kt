package com.doyoonkim.domain.interfaces.abtest

interface FirebaseRemoteConfigRepository {

    // Get raw string configuration value from the fetched result.
    fun getTestRawVariantValue(key: String): String

    // fetchAndActivate call
    // Asynchronously fetch remote configuration values from the Firebase Server.
    suspend fun fetchAndActivate(): Boolean
}
