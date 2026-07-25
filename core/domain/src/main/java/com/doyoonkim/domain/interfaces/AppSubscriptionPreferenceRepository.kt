package com.doyoonkim.domain.interfaces

import kotlinx.coroutines.flow.Flow

/**
 * @author kimdoyoon
 * Created 3/25/26 at 10:40 PM
 */
interface AppSubscriptionPreferenceRepository {

    fun getSubscribedMajor(): Flow<Set<String>>

    suspend fun updateSubscribedMajor(newMajor: String)

    fun getSubscriptionPending(): Flow<Set<String>>

    suspend fun addPendingTarget(target: String)
    suspend fun removePendingTarget(completed: Set<String>)

    fun getUnsubscribePending(): Flow<Set<String>>

    suspend fun removeUnsubscribedTarget(completed: Set<String>)

    suspend fun updateUnsubscribeTarget(target: String)
}
