package com.doyoonkim.main.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchNoticesByKeywordImpl
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.Dispatchers
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
import javax.inject.Inject

class NoticeSearchViewModel @Inject constructor(
    private val fetchNoticesByKeyword: FetchNoticesByKeywordImpl
) : ViewModel() {

    private var _uiState = MutableStateFlow(NoticeSearchState())
    val uiState = _uiState.asStateFlow()

    fun searchNoticeUsingKeyword(keyword: String) {
        viewModelScope.launch {
            fetchNoticesByKeyword(keyword)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    _uiState.update {
                        it.copy(
                            isFetching = false,
                            fetchResult = result
                        )
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    suspend fun observeKeywordInput() = snapshotFlow { uiState.value.searchKeyword }
        .debounce(500L)
        .distinctUntilChanged()
        .filter { it.isNotBlank() }
        .collectLatest {
            searchNoticeUsingKeyword(it)
        }

    fun updateSearchKeyword(newKeyword: String) {
        _uiState.update {
            it.copy(
                searchKeyword = newKeyword
            )
        }
    }

    private fun updateFetchingStatus(status: Boolean) = _uiState.update { it.copy(isFetching = status) }

}

data class NoticeSearchState(
    val searchKeyword: String = "",
    val isFetching: Boolean = false,
    val fetchResult: List<NoticeVO> = emptyList()
)