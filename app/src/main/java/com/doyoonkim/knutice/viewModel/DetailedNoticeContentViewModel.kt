package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _uiState = MutableStateFlow<DetailedContentState>(DetailedContentState())
    val uiState = _uiState.asStateFlow()

    private val requested = savedStateHandle.toRoute<FullContent>()

    fun requestFullContent() {
        CoroutineScope(Dispatchers.IO).launch {
            crawlFullContentUseCase.getFullContentFromSource(
                requested.title ?: "", requested.info ?: "", requested.url
            )
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { content ->
                            _uiState.update {
                                it.copy(
                                    title = content.title,
                                    info = content.info,
                                    fullContent = content.fullContent,
                                    fullContentUrl = content.fullContentUrl
                                )
                            }
                        },
                        onFailure = {
                            Log.d("DetailedNoticeContentVM", "Unable to get Full Content.")
                        }
                    )
                }
        }
    }
}