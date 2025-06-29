package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.domain.usecases.SyncDataWithUpdateDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val syncDataWithUpdateDatabase: SyncDataWithUpdateDatabase,
    private val tokenHandler: TokenHandler
) : ViewModel() {

    private var _uiState = MutableStateFlow(SplashState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (!appPreferences.isDatabaseSyncCompleted()) {
                _uiState.update {
                    it.copy(
                        syncStatus = SyncStatus.PROCESSING
                    )
                }
                syncDataWithUpdateDatabase.entrySync()
                    .collectLatest { result ->
                        // result: Pair<Boolean, Boolean> (SyncCompleted, PartialFailed)
                        appPreferences.setSyncStatus_1_2(result.completed)
                        appPreferences.setDatabaseSyncPartialFailedStatus(result.withError)

                        if (result.completed) {
                            _uiState.update {
                                it.copy(
                                    syncStatus = SyncStatus.COMPLETED
                                )
                            }
                        }
                    }
            } else {
                _uiState.update {
                    it.copy(
                        syncStatus = SyncStatus.COMPLETED
                    )
                }
            }
            requestTokenValidation()
        }.invokeOnCompletion {
            Log.d("SplashViewModel", "OnComplete with ${uiState.value.toString()}")
            isPreProcessingCompleted()
        }
    }

    // Should be performed very last.
    private suspend fun requestTokenValidation() {
        tokenHandler.handleCurrentTokenRequest()
            .collectLatest { result ->
                _uiState.update {
                    it.copy(
                        tokenValidationResult = result
                    )
                }
            }
    }

    fun isPreProcessingCompleted() {
        with(uiState.value) {
            _uiState.update {
                it.copy(
                    splashStage = if (
                        // DO NOT INCLUDE tokenValidationStatus. App should be accessed without Token Validation.
                        syncStatus == SyncStatus.COMPLETED
                    ) {
                        SplashStage.DISMISS
                    } else {
                        SplashStage.DISMISS_WITH_ERROR
                    }
                )
            }
        }
    }

}

data class SplashState(
    val splashStage: SplashStage = SplashStage.LOADING,
    val syncStatus: SyncStatus = SyncStatus.REQUESTED,
    val tokenValidationResult: Boolean = false,
)

enum class SplashStage { LOADING, DISMISS, DISMISS_WITH_ERROR }
enum class SyncStatus { REQUESTED, PROCESSING, COMPLETED }