package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.usecases.FetchBookmarkByKeyword
import com.doyoonkim.model.di.DefaultDispatcher
import com.doyoonkim.domain.usecases.FetchNoticesByKeyword
import com.doyoonkim.main.contract.FetchingSource
import com.doyoonkim.main.contract.NoticeSearchEvent
import com.doyoonkim.main.contract.NoticeSearchMutation
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
    private val fetchBookmarkByKeyword: FetchBookmarkByKeyword,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel<NoticeSearchState, NoticeSearchEvent, NoticeSearchSideEffect, NoticeSearchMutation>() {
    private val TAG = this.javaClass.name
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
        mutate(NoticeSearchMutation.Loading)

        viewModelScope.launch {
            when (uiState.value.fetchingSource) {
                FetchingSource.REMOTE -> {
                    fetchNoticesByKeyword(
                        uiState.value.searchKeyword,
                        uiState.value.fetchResult.lastOrNull()?.nttId
                    ).collectLatest { result ->
                        result.fold(
                            onSuccess = { vo ->
                                mutate(NoticeSearchMutation.Remote.Success(vo))
                            },
                            onFailure = { reason ->
                                mutate(
                                    NoticeSearchMutation.Remote.Failure(reason.stackTraceToString())
                                )
                            }
                        )
                    }
                }
                FetchingSource.LOCAL -> {
                    fetchBookmarkByKeyword(
                        uiState.value.searchKeyword,
                        20,
                        uiState.value.localFetchResult.size / 20 + 1
                    ).collectLatest { result ->
                        result.fold(
                            onSuccess = { vo ->
                                mutate(NoticeSearchMutation.Local.Success(vo))
                            },
                            onFailure = { reason ->
                                mutate(
                                    NoticeSearchMutation.Local.Failure(reason.stackTraceToString())
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // Need to have separate function for fetching notices when keyword changes and more notices requested.
    private fun searchNoticeUsingKeyword(keyword: String) {
        mutate(NoticeSearchMutation.Initialize)

        viewModelScope.launch {
            when (uiState.value.fetchingSource) {
                FetchingSource.REMOTE -> {
                    fetchNoticesByKeyword(keyword, null)
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = { vo ->
                                    mutate(NoticeSearchMutation.Remote.Success(vo))
                                },
                                onFailure = { reason ->
                                    mutate(
                                        NoticeSearchMutation.Remote.Failure(
                                            reason.stackTraceToString()
                                        )
                                    )
                                }
                            )
                        }
                }
                FetchingSource.LOCAL -> {
                    fetchBookmarkByKeyword(keyword, 20, 0)
                        .collectLatest { result ->
                            result.fold(
                                onSuccess =  { vo ->
                                    Log.d(TAG, "Received Local Search Result: ${vo.size}")
                                    vo.forEach { Log.d("TAG", it.toString()) }
                                    mutate(NoticeSearchMutation.Local.Success(vo))
                                },
                                onFailure = { reason ->
                                    mutate(
                                        NoticeSearchMutation.Local.Failure(
                                            reason.stackTraceToString()
                                        )
                                    )
                                }
                            )
                        }
                }
            }
        }.invokeOnCompletion { mutate(NoticeSearchMutation.Emptiness(isSearchResultEmpty())) }
    }

    private fun isSearchResultEmpty(): Boolean {
        return when (uiState.value.fetchingSource) {
            FetchingSource.REMOTE -> uiState.value.fetchResult.isEmpty()
            FetchingSource.LOCAL -> uiState.value.localFetchResult.isEmpty()
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
        mutate(NoticeSearchMutation.Keyword(newKeyword))
    }

    fun updateSourceStatus(index: Int) {
        mutate(NoticeSearchMutation.Source(FetchingSource.entries[index]))
        searchNoticeUsingKeyword(uiState.value.searchKeyword)
    }

    // Main Reducer
    override fun reduce(
        currentState: NoticeSearchState,
        mutation: NoticeSearchMutation
    ): NoticeSearchState {
        return when (mutation) {
            is NoticeSearchMutation.Keyword -> {
                currentState.copy(searchKeyword = mutation.keyword)
            }
            is NoticeSearchMutation.Source -> {
                currentState.copy(
                    fetchingSource = mutation.source,
                    isFetching = false,
                    isError = false,
                    canRequestMoreNotices = true
                )
            }
            is NoticeSearchMutation.Emptiness -> {
                currentState.copy(isSearchResultEmpty = mutation.status)
            }
            is NoticeSearchMutation.Initialize -> {
                currentState.copy(
                    fetchResult = emptyList(),
                    localFetchResult = emptyList()
                )
            }
            is NoticeSearchMutation.Loading -> { currentState.copy(isFetching = true) }
            is NoticeSearchMutation.Remote -> { mutation.reducer(currentState) }
            is NoticeSearchMutation.Local -> { mutation.reducer(currentState) }
        }
    }

    // Specialized Reducer
    private fun NoticeSearchMutation.Remote.reducer(state: NoticeSearchState) =
        when (this) {
            is NoticeSearchMutation.Remote.Success -> {
                state.copy(
                    isFetching = false,
                    isError = false,
                    fetchResult = state.fetchResult.toMutableList().apply {
                        addAll(result)
                    }.toList(),
                    canRequestMoreNotices = result.size == 20,
                )
            }
            is NoticeSearchMutation.Remote.Failure -> {
                state.copy(
                    isFetching = false,
                    isError = true,
                    canRequestMoreNotices = false
                ).also { Log.d(TAG, "FAILURE: $reason") }
            }
        }

    private fun NoticeSearchMutation.Local.reducer(state: NoticeSearchState) =
        when (this) {
            is NoticeSearchMutation.Local.Success -> {
                state.copy(
                    isFetching = false,
                    isError = false,
                    localFetchResult = state.localFetchResult.toMutableList().apply {
                        addAll(result)
                    }.toList(),
                    canRequestMoreNotices = result.size == 20
                )
            }
            is NoticeSearchMutation.Local.Failure -> {
                state.copy(
                    isFetching = false,
                    isError = true,
                    canRequestMoreNotices = false
                ).also { Log.d(TAG, "FAILURE: $reason") }
            }
        }
}