package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.WidgetCategoryPolicy

/**
 * @author kimdoyoon
 * Created 3/25/26 at 10:40 PM
 */
interface AppWidgetPreferenceRepository {

    fun getWidgetCategoryPolicy(): WidgetCategoryPolicy

    fun updateWidgetCategoryPolicy(policy: WidgetCategoryPolicy)
}
