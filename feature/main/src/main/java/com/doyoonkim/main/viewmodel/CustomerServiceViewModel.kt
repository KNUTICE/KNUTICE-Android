package com.doyoonkim.main.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.SubmitUserReport
import com.doyoonkim.model.requestBody.UserReportBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class CustomerServiceViewModel @Inject constructor(
    private val submitUserReport: SubmitUserReport
) : ViewModel() {

    private var _uiState = MutableStateFlow(CustomerServiceStatus())
    val uiState = _uiState.asStateFlow()

    fun updateUserReportContent(content: String) =
        _uiState.update {
            it.copy(
                userReport = content,
                exceedMinCharacters = content.length >= 5,
                reachedMaxCharacters = content.length >= 500
            )
        }

    fun submitUserReport(versionInfo: String) =
        viewModelScope.launch {
            submitUserReport(
                UserReportBody(
                    content = uiState.value.userReport,
                    deviceName = "${Build.BRAND} ${Build.MODEL}",
                    version = versionInfo
                )
            ).collectLatest { result ->
                    _uiState.update {
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

    fun resetSubmissionStatus() =
        _uiState.update {
            it.copy(
                isSubmissionFailed = false,
                isSubmissionCompleted = false
            )
        }

}

data class CustomerServiceStatus(
    val userReport: String = "",
    val reachedMaxCharacters: Boolean = false,
    val exceedMinCharacters: Boolean = false,
    val isSubmissionFailed: Boolean = false,
    val isSubmissionCompleted: Boolean = false
)