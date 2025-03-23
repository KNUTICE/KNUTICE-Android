package com.doyoonkim.knutice.viewModel

import androidx.lifecycle.ViewModel
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainServiceViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(MainServiceState())
    val uiState = _uiState.asStateFlow()

    fun updateState(
        updatedCurrentLocation: Destination = _uiState.value.currentLocation,
        updatedCurrentScaffoldTitle: String = _uiState.value.currentScaffoldTitle,
        updatedBottomNavBarVisibility: Boolean = _uiState.value.isBottomNavBarVisible,
        updatedTempReservedNoticeForBookmark: Notice = _uiState.value.tempReserveNoticeForBookmark,
        updatedCurrentTargetBookmark: Bookmark = _uiState.value.currentTargetBookmark,
        updatedScheduleTriggered: Boolean = _uiState.value.scheduleTriggered
    ) {
        _uiState.update {
            it.copy(
                currentLocation = updatedCurrentLocation,
                currentScaffoldTitle = updatedCurrentScaffoldTitle,
                isBottomNavBarVisible = updatedBottomNavBarVisibility,
                tempReserveNoticeForBookmark = updatedTempReservedNoticeForBookmark,
                currentTargetBookmark = updatedCurrentTargetBookmark,
                scheduleTriggered = updatedScheduleTriggered
            )
        }
    }
}

data class MainServiceState(
    val currentLocation: Destination = Destination.MAIN,
    val currentScaffoldTitle: String = "",
    val isBottomNavBarVisible: Boolean = false,
    val tempReserveNoticeForBookmark: Notice = Notice(),     // ?
    val currentTargetBookmark: Bookmark = Bookmark(-1),
    val scheduleTriggered: Boolean = false
)