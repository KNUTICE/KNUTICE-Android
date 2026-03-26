package com.doyoonkim.data.repository.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.doyoonkim.domain.interfaces.AppWidgetPreferenceRepository
import com.doyoonkim.model.WidgetCategoryPolicy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AppWidgetPreferenceRepositoryImpl @Inject constructor(
    private val appPref: SharedPreferences
) : AppWidgetPreferenceRepository {

    companion object {
        private const val TAG = "AppWidgetPreferenceRepositoryImpl"

        // Widget Configuration (Category Selection)
        private const val WIDGET_CATEGORY = "WIDGET_CATEGORY"

        private val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            classDiscriminator = "type"
        }

    }

    /**
     * Widget Configuration
     */
    override fun getWidgetCategoryPolicy(): WidgetCategoryPolicy {
        val cachedPolicy = appPref.getString(WIDGET_CATEGORY, null)
            ?: return WidgetCategoryPolicy.Unconfigured

        return json.decodeFromString<WidgetCategoryPolicy>(cachedPolicy)
    }

    override fun updateWidgetCategoryPolicy(policy: WidgetCategoryPolicy) {
        val serializedString = json.encodeToString<WidgetCategoryPolicy>(policy)
        appPref.edit { putString(WIDGET_CATEGORY, serializedString) }
    }

}