package com.doyoonkim.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.domain.usecases.DatabaseSyncResult
import com.doyoonkim.domain.usecases.SyncDataWithUpdateDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val syncDataWithUpdateDatabase: SyncDataWithUpdateDatabase
) : ViewModel() {

    private var _uiState = MutableStateFlow(SettingsState())
    val uiState = _uiState.asStateFlow()

    init {
        if (appPreferences.isPartialFailedDuringDatabaseSync()) {
            updateSyncNecessaryState(true)
        }
    }

    fun dismissSyncDialog() {
        _uiState.update {
            it.copy(
                isSyncDialogVisible = false
            )
        }
    }

    fun requestManualDatabaseSync() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isSyncDialogVisible = true,
                isSyncRequested = true
            )
        }

        syncDataWithUpdateDatabase.manualSync()
            .collectLatest { syncResult ->
                if (syncResult.withError) appPreferences.setDatabaseSyncPartialFailedStatus(true)
                _uiState.update {
                    it.copy(
                        isSyncRequested = false,
                        databaseSyncResult = syncResult
                    )
                }
            }
    }

    fun updateSyncNecessaryState(state: Boolean) =
        _uiState.update {
            it.copy(
                isSyncNecessary = state
            )
        }

}

data class SettingsState(
    val isSyncDialogVisible: Boolean = false,
    val isSyncNecessary: Boolean = false,
    val isSyncRequested: Boolean = false,
    val databaseSyncResult: DatabaseSyncResult = DatabaseSyncResult()
)