package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiMutation
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.model.MajorCategory
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.TipVO
import com.doyoonkim.model.TopThreeNoticeVO

data class HomeViewState(
    val mainContentState: MainContentState = MainContentState(),
    val tipState: TipState = TipState(),
    val majorNoticesState: MajorNoticesState = MajorNoticesState()
) : UiState

// Child State: Main Content (TopThreeNotices)
data class MainContentState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val notificationGeneral: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationAcademic: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationScholarship: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationEvent: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO()),
    val notificationEmployment: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO())
)

// Child State: Tip Content (Tips)
data class TipState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val tips: List<TipVO> = emptyList()
)

// Child State: Subscribed Major/College Notice
data class MajorNoticesState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val subscribed: MajorCategory = MajorCategory.UNSPECIFIED,
    val majorNotices: List<NoticeVO> = listOf(NoticeVO(), NoticeVO(), NoticeVO())
)

sealed interface HomeEvent : UiEvent {
    data object RequestMainContents : HomeEvent
    data class RequestNoticeDetail(val id: Int, val url: String) : HomeEvent
    data class RequestMore(val category: NoticeCategory) : HomeEvent
    data object RequestMoreMajorNotices : HomeEvent
    data object RequestSettings : HomeEvent
    data object RequestCarrelStatus : HomeEvent
    data object RequestDiningMenu : HomeEvent
    data class RequestTipDetail(val category: TipCategory, val url: String) : HomeEvent
    data object GoBack : HomeEvent
}

sealed class HomeSideEffect : UiSideEffect {
    data class NavToNoticeDetail(val id: Int, val url: String) : HomeSideEffect()
    data class NavToMoreNoticeInCategory(val dest: Destination) : HomeSideEffect()
    data object NavToMoreMajorNotices : HomeSideEffect()
    data class NavToTipDetail(val category: TipCategory, val url: String) : HomeSideEffect()
    data object NavToSettings : HomeSideEffect()
    data object NavToCarrelStatus : HomeSideEffect()
    data object NavToDiningMenu : HomeSideEffect()
    data object NavToBack : HomeSideEffect()
}

sealed interface HomeMutation : UiMutation {
    // Main Contents Processing
    sealed interface MainContent : HomeMutation {
        data object Loading : MainContent
        data class Success(val topThreeNotices: TopThreeNoticeVO) : MainContent
        data class Failure(val reason: String) : MainContent
    }

    // Tip Processing
    sealed interface Tip : HomeMutation {
        data object Loading : Tip
        data class Success(val tips: List<TipVO>) : Tip
        data class Failure(val reason: String) : Tip
    }

    // Major Notices Processing
    sealed interface MajorNotices : HomeMutation {
        data object Loading : MajorNotices
        data class Success(val category: MajorCategory, val notices: List<NoticeVO>) : MajorNotices
        data class Failure(val reason: String) : MajorNotices
    }
}
