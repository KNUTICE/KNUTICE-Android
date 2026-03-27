package com.doyoonkim.domain.interfaces

/**
 * @author kimdoyoon
 * Created 3/25/26 at 10:40 PM
 */
interface AppSubscriptionPreferenceRepository {

    fun getSubscribedMajor(): String?

    fun updateSubscribedMajor(newMajor: String)

}