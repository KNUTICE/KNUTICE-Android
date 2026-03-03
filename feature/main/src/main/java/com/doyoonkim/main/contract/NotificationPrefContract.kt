package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiMutation
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState

data class NotificationPrefStatus(
    val isMainNotificationPermissionGranted: Boolean = false,
    val isEachChannelAllowed: List<Boolean> = listOf(false, false, false, false, false),
    val isMajorSubscribed: Boolean = false,
    val isMajorChannelAllowed: Boolean = false,
    val isMealChannelAllowed: List<Boolean> = listOf(false, false),
    val isSyncCompleted: Boolean = true,
    val isError: Boolean = false
): UiState

sealed interface NotificationPrefEvent: UiEvent {
    data object CheckMainPermissionStatus: NotificationPrefEvent
    data object RequestSystemSettings: NotificationPrefEvent
    data object RequestTopicSubscriptionStatus: NotificationPrefEvent
    data class UpdateSubscriptionStatus(val index: Int, val value: Boolean): NotificationPrefEvent
    data class UpdateMajorSubscriptionStatue(val value: Boolean): NotificationPrefEvent
    data class UpdateMealSubscriptionStatus(val idx: Int, val value: Boolean): NotificationPrefEvent
    data object GoBack: NotificationPrefEvent
}

sealed class NotificationPrefSideEffect: UiSideEffect {
    data object NavToSystemSettings: NotificationPrefSideEffect()
    data object NavToBack: NotificationPrefSideEffect()
}

sealed interface NotificationPrefMutation: UiMutation {
    data object Syncing: NotificationPrefMutation
    data object Synced: NotificationPrefMutation
    data class MainPermission(val status: Boolean): NotificationPrefMutation

    sealed interface Notice: NotificationPrefMutation {
        data class FetchSuccess(val status: List<Boolean>): Notice
        data class UpdateSuccess(val index: Int, val status: Boolean): Notice
        data class Failure(val reason: String): Notice
    }

    sealed interface Major: NotificationPrefMutation {
        data class FetchSuccess(val status: Boolean): Major
        data class UpdateSuccess(val status: Boolean): Major
        data class Failure(val reason: String): Major
    }

    sealed interface Meal: NotificationPrefMutation {
        data class FetchSuccess(val status: List<Boolean>): Meal
        data class UpdateSuccess(val idx: Int, val status: Boolean): Meal
        data class Failure(val reason: String): Meal
    }
}