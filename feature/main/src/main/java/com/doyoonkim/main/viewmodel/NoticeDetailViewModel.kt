package com.doyoonkim.main.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchNoticeByIdImpl
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeDetailViewModel @Inject constructor(
    private val fetchNoticeById: FetchNoticeByIdImpl
) : ViewModel() {

    private var _uiState = MutableStateFlow(NoticeDetailState())
    val uiState = _uiState.asStateFlow()

    fun getTargetNoticeById(nttId:Int) =
        viewModelScope.launch {
            fetchNoticeById(nttId)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    _uiState.update {
                        it.copy(
                            receivedNotice = result,
                            isReceived = true
                        )
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
        }
    }

}

data class NoticeDetailState(
    val receivedNotice: NoticeVO? = null,
    val isReceived: Boolean = false,
    val loadingStatus: Float = 0.0f
)