package com.doyoonkim.main.viewmodel

import android.os.Trace
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.model.di.DefaultDispatcher
import com.doyoonkim.domain.usecases.FetchNoticesByKeyword
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import javax.inject.Inject

class NoticeSearchViewModel @Inject constructor(
    private val fetchNoticesByKeyword: FetchNoticesByKeyword,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _uiState = MutableStateFlow(NoticeSearchState())
    val uiState = _uiState.asStateFlow()

    fun fetchMoreNotices() {
        _uiState.update {
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
                        _uiState.update {
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
                        _uiState.update {
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
        _uiState.update {
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
                            _uiState.update {
                                it.copy(
                                    fetchResult = vo,
                                    isError = false,
                                    isFetching = false,
                                    canRequestMoreNotices = vo.size == 20
                                )
                            }
                        },
                        onFailure = {
                            _uiState.update {
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
            searchNoticeUsingKeyword(it)
        }

    fun updateSearchKeyword(newKeyword: String) {
        _uiState.update {
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

data class NoticeSearchState(
    val searchKeyword: String = "",
    val isError: Boolean = false,
    val isFetching: Boolean = false,
    val canRequestMoreNotices: Boolean = true,
    val fetchResult: List<NoticeVO> = emptyList()
)