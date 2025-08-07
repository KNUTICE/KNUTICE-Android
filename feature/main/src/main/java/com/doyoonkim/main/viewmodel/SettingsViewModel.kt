package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.domain.usecases.DatabaseSyncResult
import com.doyoonkim.domain.usecases.SyncDataWithUpdateDatabase
import com.doyoonkim.main.contract.SettingsEvent
import com.doyoonkim.main.contract.SettingsSideEffect
import com.doyoonkim.main.contract.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val syncDataWithUpdateDatabase: SyncDataWithUpdateDatabase
) : BaseViewModel<SettingsState, SettingsEvent, SettingsSideEffect>() {

    override fun setInitialState(): SettingsState = SettingsState()

    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.CheckDatabaseSyncStatus -> {
                if (appPreferences.isPartialFailedDuringDatabaseSync())
                    updateSyncNecessaryState(true)
            }
            is SettingsEvent.RequestManualSync -> requestManualDatabaseSync()
            is SettingsEvent.DismissSyncDialog -> dismissSyncDialog()
            is SettingsEvent.RequestNotificationSettings -> {
                sendSideEffect(SettingsSideEffect.NavToNotificationSettings)
            }
            is SettingsEvent.RequestCustomerService -> {
                sendSideEffect(SettingsSideEffect.NavToCustomerService)
            }
            is SettingsEvent.RequestOssNotice -> {
                sendSideEffect(SettingsSideEffect.NavToRequestOssNotice)
            }
            is SettingsEvent.GoBack -> {
                sendSideEffect(
                    SettingsSideEffect.NavToBack(
                        uiState.value.databaseSyncResult.completed
                    )
                )
            }
        }
    }

    private fun dismissSyncDialog() {
        stateUpdate {
            it.copy(
                isSyncDialogVisible = false
            )
        }
    }

    private fun requestManualDatabaseSync() = viewModelScope.launch {
        stateUpdate {
            it.copy(
                isSyncDialogVisible = true,
                isSyncRequested = true
            )
        }

        syncDataWithUpdateDatabase.manualSync()
            .collectLatest { syncResult ->
                appPreferences.setDatabaseSyncPartialFailedStatus(syncResult.withError)
                stateUpdate {
                    it.copy(
                        isSyncRequested = false,
                        databaseSyncResult = syncResult
                    )
                }
            }
    }

    private fun updateSyncNecessaryState(state: Boolean) =
        stateUpdate {
            it.copy(
                isSyncNecessary = state
            )
        }
}
