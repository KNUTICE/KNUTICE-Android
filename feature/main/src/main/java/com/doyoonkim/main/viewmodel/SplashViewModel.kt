package com.doyoonkim.main.viewmodel


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.domain.usecases.SyncDataWithUpdateDatabase
import com.doyoonkim.main.contract.SplashEvent
import com.doyoonkim.main.contract.SplashSideEffect
import com.doyoonkim.main.contract.SplashState
import com.doyoonkim.main.contract.SyncStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val syncDataWithUpdateDatabase: SyncDataWithUpdateDatabase,
    private val tokenHandler: TokenHandler
) : BaseViewModel<SplashState, SplashEvent, SplashSideEffect>() {
    override fun setInitialState(): SplashState = SplashState()

    override fun handleEvent(event: SplashEvent) {
        when (event) {
            is SplashEvent.InitiatePreprocess -> {
                checkDatabaseSyncStatus()
                startPreprocess()
            }
        }
    }

    private fun checkDatabaseSyncStatus() {
        if (appPreferences.isDatabaseSyncCompleted())
            stateUpdate {
                it.copy(syncStatus = SyncStatus.COMPLETED)
            }
    }

    private fun startPreprocess() = viewModelScope.launch {
        with(uiState.value) {
            Log.d("SplashViewModel", "Current Token: ${appPreferences.getCachedToken()}")
            // Entry Token Validation
            val tokenResult = tokenHandler.validation()
            if (!tokenResult) tokenHandler.invoke()
            delay(600L)         // Give slight delay to reduce thread workload.

            if (syncStatus == SyncStatus.REQUESTED)
                syncDatabase()
        }
    }.invokeOnCompletion {
        preProcessCompletionCheck()
    }

    private suspend fun syncDatabase() {
        stateUpdate {
            it.copy(
                syncStatus = SyncStatus.PROCESSING
            )
        }
        syncDataWithUpdateDatabase.entrySync()
            .collectLatest { result ->
                // result: Pair<Boolean, Boolean> (SyncCompleted, PartialFailed)
                appPreferences.setSyncStatus_1_2(result.completed)
                appPreferences.setSyncStatus_2_3(result.completed)
                appPreferences.setDatabaseSyncPartialFailedStatus(result.withError)

                if (result.completed) {
                    stateUpdate {
                        it.copy(
                            syncStatus = SyncStatus.COMPLETED
                        )
                    }
                }
            }
    }

    private fun preProcessCompletionCheck() {
        with(uiState.value) {
            if (
                syncStatus != SyncStatus.REQUESTED
            ) {
                if (
                    syncStatus == SyncStatus.COMPLETED
                ) {
                    sendSideEffect(SplashSideEffect.Dismiss)
                } else {
                    sendSideEffect(SplashSideEffect.DismissWithError)
                }
            }
        }
    }
}
