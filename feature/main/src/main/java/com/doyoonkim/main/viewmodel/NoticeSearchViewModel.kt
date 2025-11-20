package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.usecases.FetchBookmarkByKeyword
import com.doyoonkim.model.di.DefaultDispatcher
import com.doyoonkim.domain.usecases.FetchNoticesByKeyword
import com.doyoonkim.main.contract.NoticeSearchEvent
import com.doyoonkim.main.contract.NoticeSearchSideEffect
import com.doyoonkim.main.contract.NoticeSearchState
import com.doyoonkim.model.BookmarkAsListElementVO
import com.doyoonkim.model.NoticeVO
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
            when (uiState.value.fetchingSource) {
                FetchingSource.REMOTE -> {
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
                FetchingSource.LOCAL -> {
                    fetchBookmarkByKeyword(
                        uiState.value.searchKeyword,
                        20,
                        uiState.value.localFetchResult.size / 20 + 1
                    ).collectLatest { result ->
                        result.fold(
                            onSuccess = { vo ->
                                stateUpdate {
                                    it.copy(
                                        localFetchResult = it.localFetchResult.toMutableList().apply {
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
        }
    }

    // Need to have separate function for fetching notices when keyword changes and more notices requested.
    private fun searchNoticeUsingKeyword(keyword: String) {
        stateUpdate {
            it.copy(
                fetchResult = emptyList(),
                localFetchResult = emptyList(),
                isFetching = true
            )
        }

        viewModelScope.launch {
            when (uiState.value.fetchingSource) {
                FetchingSource.REMOTE -> {
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
                FetchingSource.LOCAL -> {
                    fetchBookmarkByKeyword(keyword, 20, 0)
                        .collectLatest { result ->
                            result.fold(
                                onSuccess =  { vo ->
                                    Log.d("NoticeSearchViewModel", "Received Local Search Result: ${vo.size}")
                                    vo.forEach { Log.d("NoticeSearchViewModel", it.toString()) }
                                    stateUpdate {
                                        it.copy(
                                            localFetchResult = vo,
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
        }.invokeOnCompletion {
            stateUpdate {
                it.copy(
                    isSearchResultEmpty = isSearchResultEmpty()
                )
            }
        }
    }

    fun isSearchResultEmpty(): Boolean {
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

    fun updateSourceStatus(index: Int) {
        _uiState.update {
            it.copy(
                fetchingSource = FetchingSource.entries[index],
                isError = false,
                isFetching = false,
                canRequestMoreNotices = true
            )
        }
        searchNoticeUsingKeyword(uiState.value.searchKeyword)
    }

}

data class NoticeSearchState(
    val searchKeyword: String = "",
    val fetchingSource: FetchingSource = FetchingSource.REMOTE,
    val isError: Boolean = false,
    val isFetching: Boolean = false,
    val isSearchResultEmpty: Boolean = false,
    val canRequestMoreNotices: Boolean = true,
    val fetchResult: List<NoticeVO> = emptyList(),
    val localFetchResult: List<BookmarkAsListElementVO> = emptyList()
)

enum class FetchingSource { REMOTE, LOCAL }