package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState

enum class SyncStatus { REQUESTED, PROCESSING, COMPLETED }
enum class TokenValidationStatus { INCOMPLETE, COMPLETED, COMPLETED_WITH_ERROR }

data class SplashState(
    val syncStatus: SyncStatus = SyncStatus.REQUESTED,
    val tokenValidationResult: TokenValidationStatus = TokenValidationStatus.INCOMPLETE,
) : UiState

sealed class SplashEvent : UiEvent {
    data object InitiatePreprocess: SplashEvent()
}

sealed class SplashSideEffect : UiSideEffect {
    data object Dismiss: SplashSideEffect()
    data object DismissWithError: SplashSideEffect()
}