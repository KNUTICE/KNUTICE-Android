package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiMutation
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.model.WidgetCategoryPolicy

data class WidgetConfigState(
    val defaultCategories: List<String> = emptyList(),
    val selectedCategoryPolicy: WidgetCategoryPolicy = WidgetCategoryPolicy.Unconfigured,
    val majorSubscribed: String? = null,
    val isProcessing: Boolean = false
) : UiState

sealed interface WidgetConfigEvent : UiEvent {
    data object FetchStatus : WidgetConfigEvent
    data class SelectPolicy(val policy: WidgetCategoryPolicy): WidgetConfigEvent
    data object SaveSelection : WidgetConfigEvent
    data object Exit : WidgetConfigEvent
}

sealed interface WidgetConfigSideEffect : UiSideEffect {
    data object ShowProcessedSnackBark: WidgetConfigSideEffect
    data object CloseSettings : WidgetConfigSideEffect
}

sealed interface WidgetConfigMutation : UiMutation {
    sealed interface Configuration: WidgetConfigMutation {
        data object Processing : Configuration
        data object Processed : Configuration
        data class Selected(val policy: WidgetCategoryPolicy): Configuration
        data class Available(val categories: List<String>, val majorSubscribed: String?): Configuration
    }
}