package com.doyoonkim.main.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.model.di.DefaultDispatcher
import com.doyoonkim.domain.usecases.FetchNoticesByKeyword
import com.doyoonkim.main.contract.NoticeSearchEvent
import com.doyoonkim.main.contract.NoticeSearchSideEffect
import com.doyoonkim.main.contract.NoticeSearchState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeSearchViewModel @Inject constructor(
    private val fetchNoticesByKeyword: FetchNoticesByKeyword,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel<NoticeSearchState, NoticeSearchEvent, NoticeSearchSideEffect>() {
    override fun setInitialState(): NoticeSearchState = NoticeSearchState()

    override fun handleEvent(event: NoticeSearchEvent) {
        when (event) {
            is NoticeSearchEvent.InitiateSearch -> {
                searchNoticeUsingKeyword(event.keyword)
            }
            is NoticeSearchEvent.UpdateSearchKeyword -> {
                updateSearchKeyword(event.value)
            }
            is NoticeSearchEvent.RequestMoreNotices -> {
                fetchMoreNotices()
            }
            is NoticeSearchEvent.RequestNoticeDetail -> {
                with (event) {
                    sendSideEffect(
                        NoticeSearchSideEffect.NavToNoticeDetail(id, url)
                    )
                }
            }
            is NoticeSearchEvent.GoBack -> sendSideEffect(NoticeSearchSideEffect.NavToBack)
        }
    }

    private fun fetchMoreNotices() {
        stateUpdate {
            it.copy(
                isFetching = true
            )
        }

        viewModelScope.launch {
            fetchNoticesByKeyword(
                uiState.value.searchKeyword,
                uiState.value.fetchResult.lastOrNull()?.nttId
            ).collectLatest { result ->
                result.fold(
                    onSuccess = { vo ->
                        stateUpdate {
                            it.copy(
                                fetchResult = it.fetchResult.toMutableList().apply {
                                    addAll(vo)
                                }.toList(),
                                isError = false,
                                isFetching = false,
                                canRequestMoreNotices = vo.size == 20
                            )
                        }
                    },
                    onFailure = {
                        stateUpdate {
                            it.copy(
                                isError = true,
                                isFetching = false,
                                canRequestMoreNotices = false
                            )
                        }
                    }
                )
            }
        }
    }

    // Need to have separate function for fetching notices when keyword changes and more notices requested.
    private fun searchNoticeUsingKeyword(keyword: String) {
        stateUpdate {
            it.copy(
                fetchResult = emptyList(),
                isFetching = true
            )
        }

        viewModelScope.launch {
            fetchNoticesByKeyword(keyword, null)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { vo ->
                            stateUpdate {
                                it.copy(
                                    fetchResult = vo,
                                    isError = false,
                                    isFetching = false,
                                    canRequestMoreNotices = vo.size == 20
                                )
                            }
                        },
                        onFailure = {
                            stateUpdate {
                                it.copy(
                                    isError = true,
                                    isFetching = false,
                                    canRequestMoreNotices = false
                                )
                            }
                        }
                    )
                }
        }
    }

    @OptIn(FlowPreview::class)
    suspend fun observeKeywordInput() = snapshotFlow { uiState.value.searchKeyword }
        .flowOn(defaultDispatcher)
        .debounce(500L)
        .distinctUntilChanged()
        .filter { it.isNotBlank() }
        .collectLatest {
            sendUiEvent(NoticeSearchEvent.InitiateSearch(it))
        }

    private fun updateSearchKeyword(newKeyword: String) {
        stateUpdate {
            it.run {
                if (fetchResult.isNotEmpty()) {
                    copy(
                        fetchResult = emptyList(),
                        searchKeyword = newKeyword
                    )
                } else {
                    copy(
                        searchKeyword = newKeyword
                    )
                }
            }
        }
    }

}