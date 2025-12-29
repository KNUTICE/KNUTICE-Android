package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState

enum class SyncStatus { REQUESTED, PROCESSING, COMPLETED }

data class SplashState(
    val syncStatus: SyncStatus = SyncStatus.REQUESTED
) : UiState

sealed class SplashEvent : UiEvent {
    data object InitiatePreprocess: SplashEvent()
}

sealed class SplashSideEffect : UiSideEffect {
    data object Dismiss: SplashSideEffect()
    data object DismissWithError: SplashSideEffect()
}