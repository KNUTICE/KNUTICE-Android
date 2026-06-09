package com.doyoonkim.data.repository.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.doyoonkim.domain.interfaces.AppSubscriptionPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class AppSubscriptionPreferenceRepositoryImpl @Inject constructor(
    private val preferenceDataStore: DataStore<Preferences>
) : AppSubscriptionPreferenceRepository {

    companion object {
        private const val TAG = "AppSubscriptionPreferenceRepositoryImpl"

        // Major Subscription Status
        @Deprecated("Use New PreferenceKey Value.")
        private const val SUBSCRIBED_MAJOR = "SUBSCRIBED_MAJOR"

        // DataStore Preferences Key
        private val MAJORS_SUBSCRIBED = stringSetPreferencesKey("MAJORS_SUBSCRIBED")
        private val MAJORS_SUBSCRIPTION_PENDING = stringSetPreferencesKey("MAJORS_SUBSCRIPTION_PENDING")
        private val MAJORS_TO_BE_UNSUBSCRIBED = stringSetPreferencesKey("MAJORS_UNSUBSCRIPTION_PENDING")
    }


    /**
     * Major Subscription Status
     */
    override fun getSubscribedMajor(): Flow<Set<String>> =
        preferenceDataStore.data.map {
            it[MAJORS_SUBSCRIBED] ?: emptySet()
        }

    override suspend fun updateSubscribedMajor(newMajor: String) {
        preferenceDataStore.edit { preferences ->
            // Subscription List for quick access on local.
            /* Logic becomes enabled once business rule updated.
                val currentTarget = preferences[MAJORS_SUBSCRIBED] ?: emptySet()
                val updatedTarget = HashSet(currentTarget).apply { add(newMajor) }
             */

            // Update majors.
            preferences[MAJORS_SUBSCRIBED] = setOf(newMajor)
        }
    }

    override fun getSubscriptionPending(): Flow<Set<String>> =
        preferenceDataStore.data.map { it[MAJORS_SUBSCRIPTION_PENDING] ?: emptySet() }

    override suspend fun addPendingTarget(target: String) {
        preferenceDataStore.edit { preferences ->
            // Pending Subscription Targets for server-side synchronization
            val currentPending = preferences[MAJORS_SUBSCRIPTION_PENDING] ?: emptySet()
            val newPending = HashSet(currentPending).apply { add(target) }

            preferences[MAJORS_SUBSCRIPTION_PENDING] = newPending
        }
    }

    override suspend fun removePendingTarget(completed: Set<String>) {
        preferenceDataStore.edit { preferences ->
            // Current Pending Status
            val currentPending = preferences[MAJORS_SUBSCRIPTION_PENDING] ?: emptySet()
            val updatedPending = HashSet(currentPending).apply { removeAll(completed) }

            // Update pending majors to be subscribed
            preferences[MAJORS_SUBSCRIPTION_PENDING] = updatedPending
        }
    }


    // Temporarily store majors to be unsubscribed.
    override fun getUnsubscribePending(): Flow<Set<String>> =
        preferenceDataStore.data.map {
            it[MAJORS_TO_BE_UNSUBSCRIBED] ?: emptySet()
        }

    override suspend fun removeUnsubscribedTarget(completed: Set<String>) {
        preferenceDataStore.edit { preferences ->
            val existingTargets = preferences[MAJORS_TO_BE_UNSUBSCRIBED] ?: emptySet()
            val updatedTargets = HashSet(existingTargets).apply { removeAll(completed) }

            preferences[MAJORS_TO_BE_UNSUBSCRIBED] = updatedTargets
        }
    }

    override suspend fun updateUnsubscribeTarget(target: String) {
        preferenceDataStore.edit { preferences ->
            val existingTargets = preferences[MAJORS_TO_BE_UNSUBSCRIBED] ?: emptySet()
            val updatedTargets = HashSet(existingTargets).apply { add(target) }

            preferences[MAJORS_TO_BE_UNSUBSCRIBED] = updatedTargets
        }
    }

}