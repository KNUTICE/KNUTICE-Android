package com.doyoonkim.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Widget Category Configuration (Handle both Fixed and Dynamic)
@Serializable
sealed interface WidgetCategoryPolicy {

    // Not Yet Configured
    @Serializable
    @SerialName("unconfigured")
    data object Unconfigured : WidgetCategoryPolicy

    // Use provided category for State update. (Fixed)
    @Serializable
    @SerialName("main")
    data class Main(val categoryKey: String) : WidgetCategoryPolicy

    // Use Major Subscription status in SharedPreference for State Update (Dynamic)
    @Serializable
    @SerialName("major")
    data object Major : WidgetCategoryPolicy
}
