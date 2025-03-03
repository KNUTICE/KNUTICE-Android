package com.doyoonkim.knutice.viewModel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.data.KnuticeRemoteSource
import com.doyoonkim.knutice.model.CustomerServiceReportState
import com.doyoonkim.knutice.model.ReportRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CustomerServiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteSource: KnuticeRemoteSource
) : ViewModel() {

    private var _uiState = MutableStateFlow(CustomerServiceReportState())
    val uiState = _uiState.asStateFlow()

    fun updateUserReportContent(content: String) {
        if (!_uiState.value.reachedMaxCharacters) {
            viewModelScope.launch(Dispatchers.Default) {
                _uiState.update {
                    it.copy(
                        userReport = content,
                        exceedMinCharacters = content.length >= 5,
                        reachedMaxCharacters = content.length >= 500
                    )
                }
            }
        }
    }

    fun updateCompletionState() {
        _uiState.update {
            it.copy(
                isSubmissionCompleted = false,
                isSubmissionFailed = false
            )
        }
    }

    fun submitUserReport() {
        val report = ReportRequest(
            content = _uiState.value.userReport,
            deviceName = "${Build.BRAND} ${Build.MODEL}",
            version = context.getString(R.string.version_code)
        )

        Log.d("CustomerServiceViewModel", "Generated Report:\n\t${report.toString()}")

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val submission = async { remoteSource.submitUserReport(report) }

                submission.await().fold(
                    onSuccess = { submissionResult ->
                        if (submissionResult) {
                            _uiState.update {
                                it.copy(
                                    userReport = "",
                                    reachedMaxCharacters = false,
                                    isSubmissionFailed = false,
                                    isSubmissionCompleted = true
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isSubmissionFailed = true,
                                    isSubmissionCompleted = true
                                )
                            }
                        }
                    },
                    onFailure = {
                        _uiState.update {
                            it.copy(
                                isSubmissionFailed = true,
                                isSubmissionCompleted = true
                            )
                        }
                    }
                )
            }
        }
    }
}