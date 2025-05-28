package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.domain.CrawlFullContentImpl
import com.doyoonkim.knutice.model.DetailedContentState
import com.doyoonkim.knutice.model.FullContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedNoticeContentViewModel @Inject constructor(
    private val crawlFullContentUseCase: CrawlFullContentImpl,
    private val repository: NoticeLocalRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _uiState = MutableStateFlow<DetailedContentState>(DetailedContentState())
    val uiState = _uiState.asStateFlow()

    private val requested = savedStateHandle.toRoute<FullContent>()

    init {
        _uiState.update {
            it.copy(
                url = requested.url ?: "www.ut.ac.kr"
            )
        }
    }

    fun updateLoadingStatus(newStatus: Int) {
        Log.d("DetailedNoticeContentViewModel", "Update loading status")
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadingStatue = (newStatus / 100).toFloat()
                )
            }
        }
    }

    // Retrieve notice by nttid
    fun getRequestedNotice(nttId: Int) {
        viewModelScope.launch {
            repository.getNoticeByNttId(nttId)
        }
    }
}