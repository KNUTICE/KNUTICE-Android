package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.domain.usecases.FetchTips
import com.doyoonkim.domain.usecases.FetchTopThreeNotices
import com.doyoonkim.main.contract.HomeEvent
import com.doyoonkim.main.contract.HomeMutation
import com.doyoonkim.main.contract.HomeSideEffect
import com.doyoonkim.main.contract.HomeViewState
import com.doyoonkim.main.contract.MainContentState
import com.doyoonkim.main.contract.TipState
import com.doyoonkim.model.NoticeCategory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val fetchTopThreeNotices: FetchTopThreeNotices,
    private val fetchTips: FetchTips
) : BaseViewModel<HomeViewState, HomeEvent, HomeSideEffect, HomeMutation>() {
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

    // Side-Effect (Access Remote Repository)
    private fun getTopThreeNotices() = viewModelScope.launch {
        fetchTopThreeNotices()
            .fold(
                onSuccess = { vo ->
                    mutate(HomeMutation.MainContent.Success(vo))
                },
                onFailure = {
                    mutate(HomeMutation.MainContent.Failure(it.stackTraceToString()))
                }
            )
    }

    // Side Effect (Access Remote Repository)
    private fun getTips() = viewModelScope.launch {
        fetchTips()
            .collectLatest { result ->
                result.fold(
                    onSuccess = { vo ->
                        mutate(HomeMutation.Tip.Success(vo))
                    },
                    onFailure = {
                        mutate(HomeMutation.Tip.Failure(it.stackTraceToString()))
                    }
                )
            }
    }

    // Main Reducer
    override fun reduce(currentState: HomeViewState, mutation: HomeMutation): HomeViewState {
        return when (mutation) {
            is HomeMutation.MainContent -> currentState.copy(
                mainContentState = mutation.reducer(currentState.mainContentState)
            )
            is HomeMutation.Tip -> currentState.copy(
                tipState = mutation.reducer(currentState.tipState)
            )
        }
    }

    // Specialized Reducers
    private fun HomeMutation.MainContent.reducer(state: MainContentState): MainContentState {
        return when (this) {
            is HomeMutation.MainContent.Loading -> state.copy(isLoading = true)
            is HomeMutation.MainContent.Success -> state.copy(
                isLoading = false,
                isError = false,
                notificationGeneral = topThreeNotices.general,
                notificationAcademic = topThreeNotices.academic,
                notificationScholarship = topThreeNotices.scholarship,
                notificationEvent = topThreeNotices.event,
                notificationEmployment = topThreeNotices.employment
            )
            is HomeMutation.MainContent.Failure -> {
                state.copy(
                    isLoading = false,
                    isError = true
                ).also {
                    Log.d(this.javaClass.name, "Fetching Main Content Failed: ${reason}")
                }
            }
        }
    }

    private fun HomeMutation.Tip.reducer(state: TipState): TipState {
        return when (this) {
            is HomeMutation.Tip.Loading -> state.copy(isLoading = true)
            is HomeMutation.Tip.Success -> state.copy(
                isLoading = false,
                isError = false,
                tips = tips
            )
            is HomeMutation.Tip.Failure -> state.copy(
                isLoading = false,
                isError = true
            )
        }
    }
}