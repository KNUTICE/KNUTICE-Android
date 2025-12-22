package com.doyoonkim.main.contract

import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.model.MajorCategory
import com.doyoonkim.model.NoticeVO

data class NoticeByMajorState(
    val lastNttId: Int = 0,
    val notices: List<NoticeVO> = emptyList(),
    val targetMajor: MajorCategory = MajorCategory.UNSPECIFIED,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isFetchable: Boolean = true,
    val isBottomSheetVisible: Boolean = false
): UiState

sealed class NoticeByMajorEvent: UiEvent {
    data object CheckSubscribedMajor: NoticeByMajorEvent()
    data object RequestRefresh: NoticeByMajorEvent()
    data object RequestSettings: NoticeByMajorEvent()
    data object RequestNotice: NoticeByMajorEvent()
    data object RequestBottomSheetExpand: NoticeByMajorEvent()
    data object RequestBottomSheetHidden: NoticeByMajorEvent()
    data object RequestGoBack: NoticeByMajorEvent()
    data class RequestNoticeDetail(val notice: NoticeVO): NoticeByMajorEvent()
    data class RequestMajorSubscriptionChange(val newTarget: MajorCategory): NoticeByMajorEvent()
}

sealed class NoticeByMajorSideEffect: UiSideEffect {
    data object NavToSettings: NoticeByMajorSideEffect()
    data object GoBack: NoticeByMajorSideEffect()
    data class NavToNotice(val nttId: Int, val url: String): NoticeByMajorSideEffect()
}
