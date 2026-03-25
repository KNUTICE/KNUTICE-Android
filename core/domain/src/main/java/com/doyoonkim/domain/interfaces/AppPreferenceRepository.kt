package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.WidgetCategoryPolicy

/**
 * @author kimdoyoon
 * Created 3/25/26 at 1:42 AM
 */
interface AppPreferenceRepository {

    fun updateDeviceToken(token: String)

    fun getCachedToken(): String?

    fun getSubscribedMajor(): String?

    fun updateSubscribedMajor(newMajor: String)

    fun getWidgetCategoryPolicy(): WidgetCategoryPolicy

    fun updateWidgetCategoryPolicy(policy: WidgetCategoryPolicy)

    fun isDatabaseSyncCompleted(): Boolean

    fun isPartialFailedDuringDatabaseSync(): Boolean

    fun setSyncStatus_1_2(status: Boolean)

    fun setSyncStatus_2_3(status: Boolean)

    fun setDatabaseSyncPartialFailedStatus(status: Boolean)

}