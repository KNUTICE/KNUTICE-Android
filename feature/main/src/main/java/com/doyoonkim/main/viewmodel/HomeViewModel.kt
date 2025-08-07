package com.doyoonkim.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.domain.usecases.FetchTips
import com.doyoonkim.domain.usecases.FetchTopThreeNotices
import com.doyoonkim.main.contract.HomeEvent
import com.doyoonkim.main.contract.HomeSideEffect
import com.doyoonkim.main.contract.HomeViewState
import com.doyoonkim.model.NoticeCategory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val fetchTopThreeNotices: FetchTopThreeNotices,
    private val fetchTips: FetchTips
) : BaseViewModel<HomeViewState, HomeEvent, HomeSideEffect>() {
    override fun setInitialState(): HomeViewState = HomeViewState()

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RequestMainContents -> {
                getTopThreeNotices()
                getTips()
            }
            is HomeEvent.RequestNoticeDetail -> {
                with (event) {
                    sendSideEffect(
                        HomeSideEffect.NavToNoticeDetail(id, url)
                    )
                }
            }
            is HomeEvent.RequestMore -> {
                with (event.category) {
                    sendSideEffect(
                        HomeSideEffect.NavToMoreNoticeInCategory(
                            when (this) {
                                NoticeCategory.GENERAL_NEWS -> Destination.MORE_GENERAL
                                NoticeCategory.ACADEMIC_NEWS -> Destination.MORE_ACADEMIC
                                NoticeCategory.SCHOLARSHIP_NEWS -> Destination.MORE_SCHOLARSHIP
                                NoticeCategory.EVENT_NEWS -> Destination.MORE_EVENT
                                NoticeCategory.EMPLOYMENT_NEWS -> Destination.MORE_EMPLOYMENT
                                else -> Destination.HOME
                            }
                        )
                    )
                }
            }
            is HomeEvent.RequestSettings -> {
                sendSideEffect(HomeSideEffect.NavToSettings)
            }
            is HomeEvent.RequestTipDetail -> {
                with (event) {
                    sendSideEffect(HomeSideEffect.NavToTipDetail(category, url))
                }
            }
            is HomeEvent.GoBack -> {
                sendSideEffect(HomeSideEffect.NavToBack)
            }
        }
    }

    private fun getTopThreeNotices() = viewModelScope.launch {
        fetchTopThreeNotices()
            .collectLatest { result ->
                result.fold(
                    onSuccess = { vo ->
                        stateUpdate {
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
                        stateUpdate {
                            it.copy(
                                isLoading = false,
                                isError = true
                            )
                        }
                    }
                )
            }
    }

    private fun getTips() = viewModelScope.launch {
        fetchTips()
            .collectLatest { result ->
                result.onSuccess { vo ->
                    stateUpdate {
                        it.copy(
                            tips = vo
                        )
                    }
                }
            }
    }
}