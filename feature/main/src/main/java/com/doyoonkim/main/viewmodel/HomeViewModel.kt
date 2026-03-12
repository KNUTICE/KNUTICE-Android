package com.doyoonkim.main.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.analytics.AnalyticsLogger
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.domain.interfaces.LocalWidgetCacheRepository
import com.doyoonkim.domain.usecases.FetchTips
import com.doyoonkim.domain.usecases.FetchTopThreeNotices
import com.doyoonkim.main.contract.HomeEvent
import com.doyoonkim.main.contract.HomeMutation
import com.doyoonkim.main.contract.HomeSideEffect
import com.doyoonkim.main.contract.HomeViewState
import com.doyoonkim.main.contract.MainContentState
import com.doyoonkim.main.contract.MajorNoticesState
import com.doyoonkim.main.contract.TipState
import com.doyoonkim.model.MajorCategory
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.WidgetCategoryPolicy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val localWidgetCacheRepository: LocalWidgetCacheRepository,
    private val fetchTopThreeNotices: FetchTopThreeNotices,
    private val fetchTips: FetchTips,
    private val appPreferences: AppPreferences,
    private val analytics: AnalyticsLogger
) : BaseViewModel<HomeViewState, HomeEvent, HomeSideEffect, HomeMutation>() {
    override fun setInitialState(): HomeViewState = HomeViewState()

    // Local Cache Management
    init {
        viewModelScope.launch {
           uiState.map { it.mainContentState to it.majorNoticesState }
               .distinctUntilChanged()
               .collectLatest { (core, major) ->
                   val coreNoticeReady = with(core) { !isLoading && !isError }
                   val majorNoticeReady = with(major) { !isLoading && !isError }

                   if (coreNoticeReady && majorNoticeReady)
                       updateNoticeLocalCache(uiState.value)
               }
        }
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.RequestMainContents -> {
                getTopThreeNotices()
                // get Major Notices
                getMajorSubscriptionStatus()
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
            is HomeEvent.RequestMoreMajorNotices -> {
                with(uiState.value.majorNoticesState) {
                    if (!isError || subscribed == MajorCategory.UNSPECIFIED) {
                        sendSideEffect(HomeSideEffect.NavToMoreMajorNotices)
                    }
                }
            }
            is HomeEvent.RequestSettings -> {
                sendSideEffect(HomeSideEffect.NavToSettings)
            }
            is HomeEvent.RequestCarrelStatus -> {
                sendSideEffect(HomeSideEffect.NavToCarrelStatus)
            }
            is HomeEvent.RequestDiningMenu -> {
                sendSideEffect(HomeSideEffect.NavToDiningMenu)
            }
            is HomeEvent.RequestTipDetail -> {
                with (event) {
                    analytics.logEvent("BROWSE_TIP", Bundle().apply {
                        putString("ITEM_CATEGORY", category.name)
                        putString("SOURCE", "HomeScreen")
                        putString("DESTINATION", url)
                    })

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
                    if (vo.isEmpty()) {
                        mutate(HomeMutation.MainContent.Failure("Received Empty List"))
                    } else {
                        mutate(HomeMutation.MainContent.Success(vo))
                    }
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

    // Side Effect (Access SharedPreference)
    private fun getMajorSubscriptionStatus() = viewModelScope.launch {
        val subscribed = appPreferences.getSubscribedMajor()?.let {
            MajorCategory.valueOf(it)
        }
        Log.d(this.javaClass.name, "Subscription: $subscribed")

        subscribed?.let {
            fetchTopThreeNotices.getMajorNotices(subscribed)
                .fold(
                    onSuccess = { result ->
                        Log.d(this.javaClass.name, "Result: ${result.toString()}")
                        mutate(HomeMutation.MajorNotices.Success(subscribed, result))
                    },
                    onFailure = { error ->
                        Log.d(this.javaClass.name, "Result: ${error.stackTraceToString()}")
                        mutate(HomeMutation.MajorNotices.Failure(error.stackTraceToString()))
                    }
                )
        } ?: mutate(HomeMutation.MajorNotices.Failure("Unable to find subscribed major"))
    }

    private fun updateNoticeLocalCache(snapshot: HomeViewState) =
        viewModelScope.launch {
            when (val noticePolicy = appPreferences.getWidgetCategoryPolicy()) {
                is WidgetCategoryPolicy.Main -> {
                    localWidgetCacheRepository.updateNoticeCache(
                        snapshot.mainContentState.get(noticePolicy.categoryKey)
                    )
                }
                is WidgetCategoryPolicy.Major -> {
                    localWidgetCacheRepository.updateNoticeCache(
                        snapshot.majorNoticesState.majorNotices
                    )
                }
                else -> {
                    // Do nothing.
                }
            }
        }

    private fun MainContentState.get(key: String) =
        when (key) {
            NoticeCategory.GENERAL_NEWS.name -> this.notificationGeneral
            NoticeCategory.ACADEMIC_NEWS.name -> this.notificationAcademic
            NoticeCategory.SCHOLARSHIP_NEWS.name -> this.notificationScholarship
            NoticeCategory.EVENT_NEWS.name -> this.notificationEvent
            NoticeCategory.EMPLOYMENT_NEWS.name -> this.notificationEmployment
            else -> emptyList()
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
            is HomeMutation.MajorNotices -> currentState.copy(
                majorNoticesState = mutation.reducer(currentState.majorNoticesState)
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

    private fun HomeMutation.MajorNotices.reducer(state: MajorNoticesState): MajorNoticesState {
        return when (this) {
            is HomeMutation.MajorNotices.Loading -> state.copy(isLoading = true)
            is HomeMutation.MajorNotices.Success -> state.copy(
                isLoading = false,
                isError = false,
                subscribed = category,
                majorNotices = notices
            )
            is HomeMutation.MajorNotices.Failure -> state.copy(
                isLoading = false,
                isError = true
            )
        }
    }
}