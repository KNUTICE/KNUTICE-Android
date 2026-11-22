package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.preferencemodel.Major
import com.doyoonkim.model.preferencemodel.SubscribedMajors
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

interface MajorSubscriptionPreferenceRepository {
    fun getSubscribedMajors(): Flow<SubscribedMajors>

    suspend fun addPendingSubscribeTarget(target: Major)

    suspend fun removePendingSubscribeTarget(completed: Set<Major>)

    suspend fun addPendingUnsubscribeTarget(target: Major)

    suspend fun removePendingUnsubscribeTarget(completed: Set<Major>)
}
