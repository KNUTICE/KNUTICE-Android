package com.doyoonkim.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchNoticeById
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeDetailViewModel @Inject constructor(
    private val fetchNoticeById: FetchNoticeById
) : ViewModel() {

    private var _uiState = MutableStateFlow(NoticeDetailState())
    val uiState = _uiState.asStateFlow()

    fun getTargetNoticeById(nttId:Int) =
        viewModelScope.launch {
            fetchNoticeById(nttId)
                .collectLatest { result ->
                    result.onSuccess { vo ->
                        _uiState.update {
                            it.copy(
                                receivedNotice = vo,
                                isReceived = true
                            )
                        }
                    }
                }
        }

    fun updateLoadingStatus(newStatus: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadingStatus = (newStatus / 100).toFloat()
                )
            }

            if (newStatus == 100) {
                delay(1000L)
                _uiState.update {
                    it.copy(
                        isLoadingCompleted = true
                    )
                }
            }
        }
    }

}

data class NoticeDetailState(
    val receivedNotice: NoticeVO? = null,
    val isReceived: Boolean = false,
    val loadingStatus: Float = 0.0f,
    val isLoadingCompleted: Boolean = false
)