package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.toRoute
import com.doyoonkim.knutice.data.KnuticeRemoteSource
import com.doyoonkim.knutice.domain.CrawlFullContentImpl
import com.doyoonkim.knutice.domain.FetchSingleNoticeImpl
import com.doyoonkim.knutice.model.DetailedContentState
import com.doyoonkim.knutice.model.FullContent
import com.doyoonkim.knutice.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
    private val fetchSingleNoticeImpl: FetchSingleNoticeImpl,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _uiState = MutableStateFlow<DetailedContentState>(DetailedContentState())
    val uiState = _uiState.asStateFlow()

    private val requested = savedStateHandle.toRoute<FullContent>()

    init {
        _uiState.update {
            it.copy(
                url = requested.url
            )
        }
        requestNoticeById(requested.nttId!!)
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

    private fun requestNoticeById(nttId: String) {
        viewModelScope.launch {
            val requested = async { fetchSingleNoticeImpl.getSingleNoticeById(nttId) }

            _uiState.update {
                it.copy(
                    requestedNotice = requested.await()
                )
            }
        }
    }
}