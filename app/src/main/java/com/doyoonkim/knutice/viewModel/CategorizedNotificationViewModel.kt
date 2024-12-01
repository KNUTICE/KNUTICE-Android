package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.domain.FetchTopThreeNoticeByCategory
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
class CategorizedNotificationViewModel @Inject constructor(
    private val fetchTopThreeNoticeUseCase: FetchTopThreeNoticeByCategory
) : ViewModel() {
    init {
        viewModelScope.launch(Dispatchers.Default) {
            fetchTopThreeNoticesPerCategory(NoticeCategory.GENERAL_NEWS)
            fetchTopThreeNoticesPerCategory(NoticeCategory.ACADEMIC_NEWS)
            fetchTopThreeNoticesPerCategory(NoticeCategory.SCHOLARSHIP_NEWS)
            fetchTopThreeNoticesPerCategory(NoticeCategory.EVENT_NEWS)
        }
    }

    private val fileName = "CategorizedNotificationViewModel"
    private val _uiState = MutableStateFlow(CategorizedNotificationState())
    var uiState: StateFlow<CategorizedNotificationState> = _uiState.asStateFlow()

    fun updateState (
        updatedNotificationGeneral: List<Notice> = _uiState.value.notificationGeneral,
        updatedNotificationAcademic: List<Notice> = _uiState.value.notificationAcademic,
        updatedNotificationScholarship: List<Notice> = _uiState.value.notificationScholarship,
        updatedNotificationEvent: List<Notice> = _uiState.value.notificationEvent
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update {
                it.copy(
                    notificationGeneral = updatedNotificationGeneral,
                    notificationAcademic = updatedNotificationAcademic,
                    notificationScholarship = updatedNotificationScholarship,
                    notificationEvent = updatedNotificationEvent
                )
            }
        }
    }

    private suspend fun fetchTopThreeNoticesPerCategory(category: NoticeCategory) {
        fetchTopThreeNoticeUseCase.getTopThreeNotices(category)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
            .collectLatest { result ->
                result.fold(
                    onSuccess =  {
                        val notices = listOf(it.notice1!!, it.notice2!!, it.notice3!!)
                        when(category) {
                            NoticeCategory.GENERAL_NEWS -> updateState(
                                updatedNotificationGeneral = notices
                            )
                            NoticeCategory.ACADEMIC_NEWS -> updateState(
                                updatedNotificationAcademic = notices
                            )
                            NoticeCategory.SCHOLARSHIP_NEWS -> updateState(
                                updatedNotificationScholarship = notices
                            )
                            NoticeCategory.EVENT_NEWS -> updateState(
                                updatedNotificationEvent = notices
                            )
                            else -> {  }
                        }
                    },
                    onFailure = {
                        Log.d(fileName, "Retrofit2: Failure: ${it.toString()}")
                    }
                )
            }
    }
}

data class CategorizedNotificationState(
    val notificationGeneral: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationAcademic: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationScholarship: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationEvent: List<Notice> = listOf(Notice(), Notice(), Notice())
)