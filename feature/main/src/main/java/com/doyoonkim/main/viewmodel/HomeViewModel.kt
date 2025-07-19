package com.doyoonkim.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchTips
import com.doyoonkim.domain.usecases.FetchTopThreeNotices
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.TipVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val fetchTopThreeNotices: FetchTopThreeNotices,
    private val fetchTips: FetchTips
) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeViewState())
    val uiState = _uiState.asStateFlow()

    fun getTopThreeNotices() = viewModelScope.launch {
        fetchTopThreeNotices()
            .collectLatest { result ->
                result.fold(
                    onSuccess = { vo ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = false,
                                notificationGeneral = vo.general,
                                notificationScholarship = vo.scholarship,
                                notificationAcademic = vo.academic,
                                notificationEvent = vo.event,
                                notificationEmployment = vo.employment
                            )
                        }
                    },
                    onFailure = {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true
                            )
                        }
                    }
                )
            }
    }

    fun getTips() = viewModelScope.launch {
        fetchTips()
            .collectLatest { result ->
                result.onSuccess { vo ->
                    _uiState.update {
                        it.copy(
                            tips = vo
                        )
                    }
                }
            }
    }
}

data class HomeViewState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val notificationGeneral: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationAcademic: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationScholarship: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationEvent: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationEmployment: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val tips: List<TipVO> = emptyList()
)