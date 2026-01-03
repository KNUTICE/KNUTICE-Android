package com.doyoonkim.main.viewmodel


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.common.di.TokenHandler
import com.doyoonkim.domain.usecases.SyncDataWithUpdateDatabase
import com.doyoonkim.main.contract.SplashEvent
import com.doyoonkim.main.contract.SplashMutation
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
) : BaseViewModel<SplashState, SplashEvent, SplashSideEffect, SplashMutation>() {
    private val TAG = this.javaClass.name
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
            mutate(SplashMutation.DatabaseSync.Completed)
    }

    private fun startPreprocess() = viewModelScope.launch {
        with(uiState.value) {
            Log.d(TAG, "Current Token: ${appPreferences.getCachedToken()}")
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
        mutate(SplashMutation.DatabaseSync.Processing)

        syncDataWithUpdateDatabase.entrySync()
            .collectLatest { result ->
                // result: Pair<Boolean, Boolean> (SyncCompleted, PartialFailed)
                appPreferences.setSyncStatus_1_2(result.completed)
                appPreferences.setSyncStatus_2_3(result.completed)
                appPreferences.setDatabaseSyncPartialFailedStatus(result.withError)

                if (result.completed) mutate(SplashMutation.DatabaseSync.Completed)
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

    // Main Reducer
    override fun reduce(currentState: SplashState, mutation: SplashMutation): SplashState {
        return when (mutation) {
            is SplashMutation.DatabaseSync -> mutation.reducer(currentState)
        }
    }

    // Specialized Reducer
    private fun SplashMutation.DatabaseSync.reducer(state: SplashState) =
        when (this) {
            is SplashMutation.DatabaseSync.Request -> {
                state.copy(syncStatus = SyncStatus.REQUESTED)
            }
            is SplashMutation.DatabaseSync.Processing -> {
                state.copy(syncStatus = SyncStatus.PROCESSING)
            }
            is SplashMutation.DatabaseSync.Completed -> {
                state.copy(syncStatus = SyncStatus.COMPLETED)
            }
        }
}
