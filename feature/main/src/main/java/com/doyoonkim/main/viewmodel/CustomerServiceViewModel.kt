package com.doyoonkim.main.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.domain.usecases.SubmitUserReport
import com.doyoonkim.main.contract.CustomerServiceEvent
import com.doyoonkim.main.contract.CustomerServiceMutation
import com.doyoonkim.main.contract.CustomerServiceSideEffect
import com.doyoonkim.main.contract.CustomerServiceStatus
import com.doyoonkim.model.requestBody.UserReportBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomerServiceViewModel @Inject constructor(
    private val submitUserReport: SubmitUserReport,
    private val appPreferences: AppPreferences
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
                mutate(CustomerServiceMutation.SubmissionStatusReset)
            }
            is CustomerServiceEvent.GoBack -> {
                sendSideEffect(CustomerServiceSideEffect.NavToBack)
            }
        }
    }

    private fun submitUserReport(versionInfo: String) =
        viewModelScope.launch {
            submitUserReport(
                UserReportBody(
                    content = uiState.value.userReport,
                    deviceName = "${Build.BRAND} ${Build.MODEL}",
                    version = versionInfo,
//                    fcmToken = appPreferences.getCachedToken()
                )
            ).collectLatest { result ->
                if (result) {
                    mutate(CustomerServiceMutation.SubmissionSuccess)
                } else {
                    mutate(CustomerServiceMutation.SubmissionFailure)
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
            is CustomerServiceMutation.SubmissionSuccess -> {
                currentState.copy(
                    userReport = "",
                    reachedMaxCharacters = false,
                    isSubmissionCompleted = true,
                    isSubmissionFailed = false
                )
            }
            is CustomerServiceMutation.SubmissionFailure -> {
                currentState.copy(
                    isSubmissionFailed = true,
                    isSubmissionCompleted = true
                )
            }
            is CustomerServiceMutation.SubmissionStatusReset -> {
                currentState.copy(
                    isSubmissionFailed = false,
                    isSubmissionCompleted = false
                )
            }
        }
    }
}