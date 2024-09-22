package com.doyoonkim.knutice.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CategorizedNotificationViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(CategorizedNotificationState())
    var uiState: StateFlow<CategorizedNotificationState> = _uiState.asStateFlow()

    fun updateState (
        updatedIsDisplayed: Boolean = _uiState.value.isDisplayed,
        updatedNotificationGeneral: List<String> = _uiState.value.notificationGeneral,
        updatedNotificationAcademic: List<String> = _uiState.value.notificationAcademic,
        updatedNotificationScholarship: List<String> = _uiState.value.notificationScholarship,
        updatedNotificationEvent: List<String> = _uiState.value.notificationEvent
    ) {
        _uiState.update {
            _uiState.value.copy(
                isDisplayed = updatedIsDisplayed,
                notificationGeneral = updatedNotificationGeneral,
                notificationAcademic = updatedNotificationAcademic,
                notificationScholarship = updatedNotificationScholarship,
                notificationEvent = updatedNotificationEvent
            )
        }
    }

}

data class CategorizedNotificationState(
    val isDisplayed: Boolean = false,
    val notificationGeneral: List<String> = listOf(),
    val notificationAcademic: List<String> = listOf(),
    val notificationScholarship: List<String> = listOf(),
    val notificationEvent: List<String> = listOf()
)