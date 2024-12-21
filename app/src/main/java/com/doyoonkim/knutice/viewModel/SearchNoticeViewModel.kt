package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.domain.QueryNoticesUsingKeyword
import com.doyoonkim.knutice.model.SearchNoticeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchNoticeViewModel @Inject constructor(
    private val queryNoticesUsingKeywordUseCase: QueryNoticesUsingKeyword
) : ViewModel() {

    // State
    private var _uiState = MutableStateFlow<SearchNoticeState>(SearchNoticeState())
    val uiState = _uiState.asStateFlow()

    fun updateKeyword(newKeyword: String) {
        _uiState.update {
            it.copy(
                searchKeyword = newKeyword
            )
        }
    }

    fun updateQueryStatue(newStatue: Boolean) = _uiState.update { it.copy(isQuerying = newStatue) }

    fun queryNoticeByKeyword(newKeyword: String) {
        Log.d("SearchNoticeViewModel", "Query keyword ($newKeyword ) would be initiated.")
        viewModelScope.launch {
            updateQueryStatue(true)
            queryNoticesUsingKeywordUseCase.getNoticesByKeyword(newKeyword)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { list ->
                            _uiState.update {
                                it.copy(
                                    queryResult = list,
                                    isQuerying = false
                                )
                            }
                        },
                        onFailure = {
                            updateQueryStatue(false)
                        }
                    )
                }
        }
    }
}