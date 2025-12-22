package com.doyoonkim.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.usecases.FetchNoticesPerPage
import com.doyoonkim.main.contract.NoticesInCategoryEvent
import com.doyoonkim.main.contract.NoticesInCategorySideEffect
import com.doyoonkim.main.contract.NoticesInCategoryState
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticesInCategoryViewModel @Inject constructor(
    private val fetchNoticesPerPage: FetchNoticesPerPage
) : BaseViewModel<NoticesInCategoryState, NoticesInCategoryEvent, NoticesInCategorySideEffect>() {
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
                requestRefresh()
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
                            stateUpdate {
                                it.copy(
                                    currentLastNttId = vo.last().nttId,
                                    notices =
                                        if (uiState.value.currentLastNttId == 0)
                                            vo
                                        else
                                            it.notices.addAll(vo),
                                    isNoticesRequested = false,
                                    isLoading = false,
                                    isRefreshRequested = false
                                )
                            }
                        },
                        onFailure = {
                            stateUpdate {
                                it.copy(
                                    isError = true,
                                    isNoticesRequested = false,
                                    isLoading = false,
                                    isRefreshRequested = false
                                )
                            }
                        }
                    )
                }
        }

    private fun requestRefresh() =
        stateUpdate {
            it.copy(
                currentLastNttId = 0,
                notices = emptyList(),
                isRefreshRequested = true,
                isError = false
            )
        }


    private fun List<NoticeVO>.addAll(extra: List<NoticeVO>) =
        List(this.size + extra.size) {
            if (it < this.size) this[it]
            else extra[it - this.size]
        }
}