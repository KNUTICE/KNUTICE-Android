package com.doyoonkim.main.viewmodel

import android.os.Build
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.interfaces.AppPreferenceRepository
import com.doyoonkim.domain.usecases.SubmitUserReport
import com.doyoonkim.main.contract.CustomerServiceEvent
import com.doyoonkim.main.contract.CustomerServiceMutation
import com.doyoonkim.main.contract.CustomerServiceSideEffect
import com.doyoonkim.main.contract.CustomerServiceStatus
import com.doyoonkim.model.requestBody.UserReportBody
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomerServiceViewModel @Inject constructor(
    private val submitUserReport: SubmitUserReport,
    private val appPreferences: AppPreferenceRepository
) : BaseViewModel<CustomerServiceStatus, CustomerServiceEvent, CustomerServiceSideEffect, CustomerServiceMutation>() {
    override fun setInitialState(): CustomerServiceStatus = CustomerServiceStatus()

    override fun handleEvent(event: CustomerServiceEvent) {
        when (event) {
            is CustomerServiceEvent.UpdateUserReport -> {
                mutate(CustomerServiceMutation.UpdateReportContent(event.value))
            }
            is CustomerServiceEvent.SubmitUserReport -> {
                submitUserReport(event.version)
            }
            is CustomerServiceEvent.ResetSubmissionStatus -> {
                mutate(CustomerServiceMutation.Submit.SubmissionStatusReset)
            }
            is CustomerServiceEvent.GoBack -> {
                sendSideEffect(CustomerServiceSideEffect.NavToBack)
            }
        }
    }

    private fun submitUserReport(versionInfo: String) {
        mutate(CustomerServiceMutation.Submit.Processing)
        viewModelScope.launch {
            submitUserReport(
                UserReportBody(
                    content = uiState.value.userReport,
                    deviceName = "${Build.BRAND} ${Build.MODEL}",
                    version = versionInfo
                )
            ).collectLatest { result ->
                if (result) {
                    mutate(CustomerServiceMutation.Submit.Success)
                } else {
                    mutate(CustomerServiceMutation.Submit.Failure)
                }
            }
        }
    }

    // Main Reducer
    override fun reduce(
        currentState: CustomerServiceStatus,
        mutation: CustomerServiceMutation
    ): CustomerServiceStatus {
        return when (mutation) {
            is CustomerServiceMutation.UpdateReportContent -> {
                currentState.copy(
                    userReport = mutation.content,
                    exceedMinCharacters = mutation.content.length >= 5,
                    reachedMaxCharacters = mutation.content.length >= 500
                )
            }
            is CustomerServiceMutation.Submit -> mutation.reducer(currentState)
        }
    }

    private fun CustomerServiceMutation.Submit.reducer(
        currentState: CustomerServiceStatus
    ): CustomerServiceStatus {
        return when (this) {
            is CustomerServiceMutation.Submit.Processing -> {
                currentState.copy(
                    isSubmissionProcessing = true,
                    isSubmissionCompleted = false,
                    isSubmissionSuccess = false
                )
            }
            is CustomerServiceMutation.Submit.Success -> {
                currentState.copy(
                    isSubmissionProcessing = false,
                    isSubmissionCompleted = true,
                    isSubmissionSuccess = true
                )
            }
            is CustomerServiceMutation.Submit.Failure -> {
                currentState.copy(
                    isSubmissionProcessing = false,
                    isSubmissionCompleted = true,
                    isSubmissionSuccess = false
                )
            }
            is CustomerServiceMutation.Submit.SubmissionStatusReset -> {
                currentState.copy(
                    userReport = "",
                    reachedMaxCharacters = false,
                    exceedMinCharacters = false,
                    isSubmissionProcessing = false,
                    isSubmissionCompleted = false,
                    isSubmissionSuccess = false
                )
            }
        }
    }
}