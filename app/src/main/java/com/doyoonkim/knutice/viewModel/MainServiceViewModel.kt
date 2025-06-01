package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.data.KnuticeRemoteSource
import com.doyoonkim.knutice.domain.FetchSingleNoticeImpl
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainServiceViewModel @Inject constructor(
    private val fetchSingleNotice: FetchSingleNoticeImpl
) : ViewModel() {
    private var _uiState = MutableStateFlow(MainServiceState())
    val uiState = _uiState.asStateFlow()

    // Targeted to be separated. (Causes multiple recomposition & state updates.)
    fun updateState(
        updatedCurrentLocation: Destination = _uiState.value.currentLocation,
        updatedCurrentScaffoldTitle: String = _uiState.value.currentScaffoldTitle,
        updatedBottomNavBarVisibility: Boolean = _uiState.value.isBottomNavBarVisible,
        updatedFabVisibility: Boolean = _uiState.value.isFabVisible,
        updatedTempReservedNoticeForBookmark: Notice = _uiState.value.tempReserveNoticeForBookmark,
        updatedCurrentTargetBookmark: Bookmark = _uiState.value.currentTargetBookmark,
        updatedScheduleTriggered: Boolean = _uiState.value.scheduleTriggered
    ) {
        _uiState.update {
            it.copy(
                currentLocation = updatedCurrentLocation,
                currentScaffoldTitle = updatedCurrentScaffoldTitle,
                isBottomNavBarVisible = updatedBottomNavBarVisibility,
                isFabVisible = updatedFabVisibility,
                tempReserveNoticeForBookmark = updatedTempReservedNoticeForBookmark,
                currentTargetBookmark = updatedCurrentTargetBookmark,
                scheduleTriggered = updatedScheduleTriggered
            )
        }
    }

    fun updateLanguageModelDownloadStatus(newStatus: String) {
        _uiState.update {
            it.copy(
                languageModelDownloadResult = newStatus
            )
        }
    }

    fun getReservedNotice(nttId: String) {
        viewModelScope.launch {
            Log.d("MainServiceViewModel", "Start request reserved notice")
            val request = async {
                fetchSingleNotice.getSingleNoticeById(nttId)
            }.await()

            _uiState.update {
                it.copy(
                    tempReserveNoticeForBookmark = request,
                    currentScaffoldTitle = request.title
                )
            }
        }
    }
}

data class MainServiceState(
    val currentLocation: Destination = Destination.MAIN,
    val currentScaffoldTitle: String = "",
    val isBottomNavBarVisible: Boolean = false,
    val isFabVisible: Boolean = true,
    val tempReserveNoticeForBookmark: Notice = Notice(),     // For navigation to Edit Bookmark from DetailedContent (Since Detailed content requires FullContent to enter, while Edit Bookmark requires Notice to enter.
    val currentTargetBookmark: Bookmark = Bookmark(-1),
    val scheduleTriggered: Boolean = false,
    val languageModelDownloadResult: String = "YET_STARTED"
)