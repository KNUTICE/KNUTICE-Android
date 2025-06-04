package com.doyoonkim.main.viewmodel

import androidx.lifecycle.ViewModel
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
//import javax.inject.Inject


class HomeViewModel constructor(

) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeViewState())
    val uiState = _uiState.asStateFlow()

}

data class HomeViewState(
    val notificationGeneral: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationAcademic: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationScholarship: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationEvent: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO())
)