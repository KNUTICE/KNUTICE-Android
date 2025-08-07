package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState

data class NotificationPrefStatus(
    val isMainNotificationPermissionGranted: Boolean = false,
    val isEachChannelAllowed: List<Boolean> = listOf(false, false, false, false, false),
    val isSyncCompleted: Boolean = false,
    val isError: Boolean = false
): UiState

sealed class NotificationPrefEvent: UiEvent {
    data object CheckMainPermissionStatus: NotificationPrefEvent()
    data object RequestSystemSettings: NotificationPrefEvent()
    data object RequestTopicSubscriptionStatus: NotificationPrefEvent()
    data class UpdateSubscriptionStatus(val index: Int, val value: Boolean): NotificationPrefEvent()
    data object GoBack: NotificationPrefEvent()
}

sealed class NotificationPrefSideEffect: UiSideEffect {
    data object NavToSystemSettings: NotificationPrefSideEffect()
    data object NavToBack: NotificationPrefSideEffect()
}