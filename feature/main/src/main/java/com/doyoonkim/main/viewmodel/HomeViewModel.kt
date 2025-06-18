package com.doyoonkim.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchTopThreeNoticesImpl
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

//import javax.inject.Inject


class HomeViewModel @Inject constructor(
    private val fetchTopThreeNotices: FetchTopThreeNoticesImpl
) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeViewState())
    val uiState = _uiState.asStateFlow()

    fun getTopThreeNotices() = viewModelScope.launch {
        fetchTopThreeNotices()
            .flowOn(Dispatchers.IO)
            .collectLatest { vo ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notificationGeneral = vo.general,
                        notificationScholarship = vo.scholarship,
                        notificationAcademic = vo.academic,
                        notificationEvent = vo.event
                    )
                }
            }
    }
}

data class HomeViewState(
    val isLoading: Boolean = true,
    val notificationGeneral: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationAcademic: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationScholarship: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationEvent: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO())
)