package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.TipVO

data class HomeViewState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val notificationGeneral: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationAcademic: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationScholarship: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationEvent: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationEmployment: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val tips: List<TipVO> = emptyList()
): UiState

sealed class HomeEvent : UiEvent {
    data object RequestMainContents: HomeEvent()
    data class RequestNoticeDetail(val id: Int, val url: String): HomeEvent()
    data class RequestMore(val category: NoticeCategory): HomeEvent()
    data object RequestSettings: HomeEvent()
    data class RequestTipDetail(val category: TipCategory, val url: String): HomeEvent()
    data object GoBack: HomeEvent()
}

sealed class HomeSideEffect : UiSideEffect {
    data class NavToNoticeDetail(val id: Int, val url: String): HomeSideEffect()
    data class NavToMoreNoticeInCategory(val dest: Destination): HomeSideEffect()
    data class NavToTipDetail(val category: TipCategory, val url: String): HomeSideEffect()
    data object NavToSettings: HomeSideEffect()
    data object NavToBack: HomeSideEffect()
}