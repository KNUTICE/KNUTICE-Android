package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.doyoonkim.knutice.domain.FetchNoticesPerPageInCategory
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.model.NavDestination
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreCategorizedNotificationViewModel @Inject constructor(
    private val fetchListOfNoticesUseCase: FetchNoticesPerPageInCategory,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val filename = "MoreCategorizedNotificationViewModel"

    // UI State
    private var _uiState = MutableStateFlow<MoreNotificationListState>(MoreNotificationListState())
    val uiState = _uiState.asStateFlow()

    // Category of Requested Notice List
    private val category = savedStateHandle.toRoute<NavDestination>().run {
        when (this.arrived) {
            Destination.MORE_GENERAL -> NoticeCategory.GENERAL_NEWS
            Destination.MORE_ACADEMIC -> NoticeCategory.ACADEMIC_NEWS
            Destination.MORE_SCHOLARSHIP -> NoticeCategory.SCHOLARSHIP_NEWS
            Destination.MORE_EVENT -> NoticeCategory.EVENT_NEWS
            else -> NoticeCategory.Unspecified
        }
    }

    fun requestRefresh() {
        _uiState.update {
            it.copy(
                currentLastNttId =  0,
                notices = List<Notice>(20) { Notice() },
                isRefreshRequested = true
            )
        }
        fetchNotificationPerPage()
    }

    fun requestMoreNotices() {
        if (!_uiState.value.isLoading) {
            fetchNotificationPerPage()
        }
    }

    fun fetchNotificationPerPage() {
        CoroutineScope(Dispatchers.IO).launch {
            fetchListOfNoticesUseCase.getNoticesPerPage(
                category, _uiState.value.currentLastNttId
            )
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { received ->
                            _uiState.update {
                                it.copy(
                                    currentLastNttId = received.last().nttId,
                                    notices = if (_uiState.value.currentLastNttId == 0) {
                                        received
                                    } else {
                                        it.notices.addAll(received)
                                    },
                                    isLoading = false,
                                    isRefreshRequested = false
                                )
                            }
                        },
                        onFailure = { exception ->
                            Log.d(filename, "Unable to received data.\nReason: ${exception.message}")
                            _uiState.update {
                                it.copy(isLoading = false, isRefreshRequested = false)
                            }
                        }
                    )
                }
        }
    }

    private fun List<Notice>.addAll(additionalElements: List<Notice>): List<Notice> {
        return List<Notice>(this.size + additionalElements.size) {
            if (it in indices) this[it]
            else additionalElements[it - this.size]
        }
    }

}

data class MoreNotificationListState(
    val currentLastNttId: Int = 0,
    val notices: List<Notice> = List<Notice>(20) { Notice() },
    val isLoading: Boolean = false,
    val isRefreshRequested: Boolean = false
)