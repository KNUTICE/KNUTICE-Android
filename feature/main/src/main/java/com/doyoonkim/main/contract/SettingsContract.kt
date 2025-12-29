package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.domain.usecases.DatabaseSyncResult

data class SettingsState(
    val isSyncDialogVisible: Boolean = false,
    val isSyncNecessary: Boolean = false,
    val isSyncRequested: Boolean = false,
    val databaseSyncResult: DatabaseSyncResult = DatabaseSyncResult()
) : UiState

sealed class SettingsEvent: UiEvent {
    data object CheckDatabaseSyncStatus: SettingsEvent()
    data object RequestManualSync: SettingsEvent()
    data object DismissSyncDialog: SettingsEvent()
    data object RequestNotificationSettings: SettingsEvent()
    data object RequestCustomerService: SettingsEvent()
    data object RequestOssNotice: SettingsEvent()
    data object GoBack: SettingsEvent()
}

sealed class SettingsSideEffect: UiSideEffect {
    data object NavToNotificationSettings: SettingsSideEffect()
    data object NavToCustomerService: SettingsSideEffect()
    data object NavToRequestOssNotice: SettingsSideEffect()
    data class NavToBack(val status: Boolean): SettingsSideEffect()
}