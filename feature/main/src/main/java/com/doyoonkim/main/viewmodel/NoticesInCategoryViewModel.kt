package com.doyoonkim.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchNoticesPerPage
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticesInCategoryViewModel @Inject constructor(
    private val fetchNoticesPerPage: FetchNoticesPerPage
) : ViewModel() {

    // uiStates
    private var _uiState = MutableStateFlow(NoticesInCategoryStates())
    val uiState = _uiState.asStateFlow()

    fun getNoticesPerPageInCategory(category: NoticeCategory) =
        viewModelScope.launch {
            fetchNoticesPerPage(category, uiState.value.currentLastNttId)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { vo ->
                            _uiState.update {
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
                            _uiState.update {
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

    fun requestRefresh() =
        _uiState.update {
            it.copy(
                currentLastNttId = 0,
                notices = emptyList(),
                isRefreshRequested = true,
                isError = false
            )
        }

    // TODO Subject to be removed.
    fun requestMoreNotices() {
        // TODO Need to fix the way to pass the NoticeCategory parameter.
        if (!uiState.value.isLoading) _uiState.update { it.copy(isNoticesRequested = true) }
    }


    private fun List<NoticeVO>.addAll(extra: List<NoticeVO>) =
        List(this.size + extra.size) {
            if (it < this.size) this[it]
            else extra[it - this.size]
        }

}

data class NoticesInCategoryStates(
    val currentLastNttId: Int = 0,
    val notices: List<NoticeVO> = List(20) { NoticeVO() },
    val isNoticesRequested: Boolean = true,
    val isError: Boolean = false,
    val isLoading: Boolean = true,
    val isRefreshRequested: Boolean = false
)