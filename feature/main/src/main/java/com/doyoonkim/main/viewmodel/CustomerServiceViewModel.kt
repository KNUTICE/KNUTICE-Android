package com.doyoonkim.main.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.usecases.SubmitUserReport
import com.doyoonkim.main.contract.CustomerServiceEvent
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
    private val submitUserReport: SubmitUserReport
) : BaseViewModel<CustomerServiceStatus, CustomerServiceEvent, CustomerServiceSideEffect>() {
    override fun setInitialState(): CustomerServiceStatus = CustomerServiceStatus()

    override fun handleEvent(event: CustomerServiceEvent) {
        when (event) {
            is CustomerServiceEvent.UpdateUserReport -> {
                updateUserReportContent(event.value)
            }
            is CustomerServiceEvent.SubmitUserReport -> {
                submitUserReport(event.version)
            }
            is CustomerServiceEvent.ResetSubmissionStatus -> {
                resetSubmissionStatus()
            }
            is CustomerServiceEvent.GoBack -> {
                sendSideEffect(CustomerServiceSideEffect.NavToBack)
            }
        }
    }

    private fun updateUserReportContent(content: String) =
        stateUpdate {
            it.copy(
                userReport = content,
                exceedMinCharacters = content.length >= 5,
                reachedMaxCharacters = content.length >= 500
            )
        }

    private fun submitUserReport(versionInfo: String) =
        viewModelScope.launch {
            submitUserReport(
                UserReportBody(
                    content = uiState.value.userReport,
                    deviceName = "${Build.BRAND} ${Build.MODEL}",
                    version = versionInfo
                )
            ).collectLatest { result ->
                    stateUpdate {
                        if (result) {
                            it.copy(
                                userReport = "",
                                reachedMaxCharacters = false,
                                isSubmissionCompleted = true,
                                isSubmissionFailed = false
                            )
                        } else {
                            it.copy(
                                isSubmissionFailed = true,
                                isSubmissionCompleted = true
                            )
                        }
                    }
                }
        }

    private fun resetSubmissionStatus() =
        stateUpdate {
            it.copy(
                isSubmissionFailed = false,
                isSubmissionCompleted = false
            )
        }

}