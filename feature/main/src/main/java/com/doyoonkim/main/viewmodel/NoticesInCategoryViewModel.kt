package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.usecases.FetchNoticesPerPage
import com.doyoonkim.main.contract.NoticesInCategoryEvent
import com.doyoonkim.main.contract.NoticesInCategoryMutation
import com.doyoonkim.main.contract.NoticesInCategorySideEffect
import com.doyoonkim.main.contract.NoticesInCategoryState
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticesInCategoryViewModel @Inject constructor(
    private val fetchNoticesPerPage: FetchNoticesPerPage
) : BaseViewModel<NoticesInCategoryState, NoticesInCategoryEvent, NoticesInCategorySideEffect, NoticesInCategoryMutation>() {
    private val TAG = this.javaClass.name
    override fun setInitialState(): NoticesInCategoryState = NoticesInCategoryState()

    override fun handleEvent(event: NoticesInCategoryEvent) {
        when (event) {
            is NoticesInCategoryEvent.RequestNotices -> {
                getNoticesPerPageInCategory(event.category)
            }
            is NoticesInCategoryEvent.RequestNoticeDetail -> {
                if (!uiState.value.isLoading) {
                    with(event) {
                        sendSideEffect(NoticesInCategorySideEffect.NavToNoticeDetail(id, url))
                    }
                }
            }
            is NoticesInCategoryEvent.RequestRefresh -> {
                mutate(NoticesInCategoryMutation.Refreshing)
            }
            is NoticesInCategoryEvent.GoBack -> {
                sendSideEffect(NoticesInCategorySideEffect.NavToBack)
            }
        }
    }

    private fun getNoticesPerPageInCategory(category: NoticeCategory) =
        viewModelScope.launch {
            fetchNoticesPerPage(category.name, uiState.value.currentLastNttId)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { vo ->
                            mutate(NoticesInCategoryMutation.Success(vo))
                        },
                        onFailure = { reason ->
                            mutate(NoticesInCategoryMutation.Failure(reason.stackTraceToString()))
                        }
                    )
                }
        }

    private fun List<NoticeVO>.addAll(extra: List<NoticeVO>) =
        List(this.size + extra.size) {
            if (it < this.size) {
                this[it]
            } else {
                extra[it - this.size]
            }
        }

    // Main Reducer
    override fun reduce(
        currentState: NoticesInCategoryState,
        mutation: NoticesInCategoryMutation
    ): NoticesInCategoryState {
        return when (mutation) {
            is NoticesInCategoryMutation.Loading -> {
                currentState.copy(isLoading = true)
            }
            is NoticesInCategoryMutation.Refreshing -> {
                currentState.copy(
                    isRefreshing = true,
                    currentLastNttId = 0,
                    notices = List(20) { NoticeVO() }
                )
            }
            is NoticesInCategoryMutation.Success -> {
                currentState.copy(
                    isLoading = false,
                    isError = false,
                    isRefreshing = false,
                    currentLastNttId = mutation.notices.last().nttId,
                    notices = if (currentState.currentLastNttId == 0) {
                        mutation.notices
                    } else {
                        currentState.notices.addAll(mutation.notices)
                    }
                )
            }
            is NoticesInCategoryMutation.Failure -> {
                currentState.copy(
                    isLoading = false,
                    isError = true,
                    isRefreshing = false
                ).also { Log.d(TAG, "FAILURE: ${mutation.reason}") }
            }
        }
    }
}
