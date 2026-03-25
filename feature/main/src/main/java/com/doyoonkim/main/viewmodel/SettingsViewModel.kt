package com.doyoonkim.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.interfaces.AppPreferenceRepository
import com.doyoonkim.domain.usecases.SyncDataWithUpdateDatabase
import com.doyoonkim.main.contract.SettingsEvent
import com.doyoonkim.main.contract.SettingsMutation
import com.doyoonkim.main.contract.SettingsSideEffect
import com.doyoonkim.main.contract.SettingsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferenceRepository,
    private val syncDataWithUpdateDatabase: SyncDataWithUpdateDatabase
) : BaseViewModel<SettingsState, SettingsEvent, SettingsSideEffect, SettingsMutation>() {
    private val TAG = this.javaClass.name
    override fun setInitialState(): SettingsState = SettingsState()

    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.CheckDatabaseSyncStatus -> {
                if (appPreferences.isPartialFailedDuringDatabaseSync())
                    mutate(SettingsMutation.Database.SyncNeeded)
            }
            is SettingsEvent.RequestManualSync -> requestManualDatabaseSync()
            is SettingsEvent.DismissSyncDialog -> mutate(SettingsMutation.Database.Dismiss)
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

    private fun requestManualDatabaseSync() = viewModelScope.launch {
        mutate(SettingsMutation.Database.Syncing)

        syncDataWithUpdateDatabase.manualSync()
            .collectLatest { syncResult ->
                appPreferences.setDatabaseSyncPartialFailedStatus(syncResult.withError)
                mutate(SettingsMutation.Database.Synced(syncResult))
            }
    }

    // Main Reducer
    override fun reduce(currentState: SettingsState, mutation: SettingsMutation): SettingsState {
        return when (mutation) {
            is SettingsMutation.Database -> mutation.reducer(currentState)
        }
    }

    // Specialized Reducer
    private fun SettingsMutation.Database.reducer(state: SettingsState) =
        when (this) {
            is SettingsMutation.Database.SyncNeeded -> { state.copy(isSyncNecessary = true) }
            is SettingsMutation.Database.Syncing -> {
                state.copy(
                    isSyncDialogVisible = true,
                    isSyncRequested = true
                )
            }
            is SettingsMutation.Database.Synced -> {
                state.copy(
                    isSyncRequested = false,
                    databaseSyncResult = result
                )
            }
            is SettingsMutation.Database.Dismiss -> { state.copy(isSyncDialogVisible = false) }
        }
}
