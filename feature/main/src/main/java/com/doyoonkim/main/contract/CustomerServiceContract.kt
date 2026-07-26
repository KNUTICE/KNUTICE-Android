package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiMutation
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState

data class CustomerServiceStatus(
    val userReport: String = "",
    val reachedMaxCharacters: Boolean = false,
    val exceedMinCharacters: Boolean = false,
    val isSubmissionProcessing: Boolean = false,
    val isSubmissionCompleted: Boolean = false,
    val isSubmissionSuccess: Boolean = false
) : UiState

sealed interface CustomerServiceEvent : UiEvent {
    data class UpdateUserReport(val value: String) : CustomerServiceEvent
    data class SubmitUserReport(val version: String) : CustomerServiceEvent
    data object ResetSubmissionStatus : CustomerServiceEvent
    data object GoBack : CustomerServiceEvent
}

sealed class CustomerServiceSideEffect : UiSideEffect {
    data object NavToBack : CustomerServiceSideEffect()
}

sealed interface CustomerServiceMutation : UiMutation {
    data class UpdateReportContent(val content: String) : CustomerServiceMutation

    sealed interface Submit : CustomerServiceMutation {
        data object Processing : Submit
        data object Success : Submit
        data object Failure : Submit
        data object SubmissionStatusReset : Submit
    }
}
