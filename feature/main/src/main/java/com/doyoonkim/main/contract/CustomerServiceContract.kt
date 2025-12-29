package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState

data class CustomerServiceStatus(
    val userReport: String = "",
    val reachedMaxCharacters: Boolean = false,
    val exceedMinCharacters: Boolean = false,
    val isSubmissionFailed: Boolean = false,
    val isSubmissionCompleted: Boolean = false
): UiState

sealed class CustomerServiceEvent : UiEvent {
    data class UpdateUserReport(val value: String): CustomerServiceEvent()
    data class SubmitUserReport(val version: String): CustomerServiceEvent()
    data object ResetSubmissionStatus: CustomerServiceEvent()
    data object GoBack: CustomerServiceEvent()
}

sealed class CustomerServiceSideEffect: UiSideEffect {
    data object NavToBack: CustomerServiceSideEffect()
}