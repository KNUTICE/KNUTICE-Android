package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.doyoonkim.knutice.domain.FetchTopThreeNoticeByCategory
import com.doyoonkim.knutice.domain.Notice
import com.doyoonkim.knutice.domain.NoticeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategorizedNotificationViewModel @Inject constructor(
    private val fetchTopThreeNoticeUseCase: FetchTopThreeNoticeByCategory
) : ViewModel() {
    init {
        fetchTopThreeNotice(NoticeCategory.GENERAL)
        fetchTopThreeNotice(NoticeCategory.ACADEMIC)
        fetchTopThreeNotice(NoticeCategory.SCHOLARSHIP)
        fetchTopThreeNotice(NoticeCategory.EVENT)
    }

    private val fileName = "CategorizedNotificationViewModel"
    private val _uiState = MutableStateFlow(CategorizedNotificationState())
    var uiState: StateFlow<CategorizedNotificationState> = _uiState.asStateFlow()

    fun updateState (
        updatedIsDisplayed: Boolean = _uiState.value.isDisplayed,
        updatedNotificationGeneral: List<Notice> = _uiState.value.notificationGeneral,
        updatedNotificationAcademic: List<Notice> = _uiState.value.notificationAcademic,
        updatedNotificationScholarship: List<Notice> = _uiState.value.notificationScholarship,
        updatedNotificationEvent: List<Notice> = _uiState.value.notificationEvent
    ) {
        _uiState.update {
            it.copy(
                isDisplayed = updatedIsDisplayed,
                notificationGeneral = updatedNotificationGeneral,
                notificationAcademic = updatedNotificationAcademic,
                notificationScholarship = updatedNotificationScholarship,
                notificationEvent = updatedNotificationEvent
            )
        }
    }

    fun fetchTopThreeNotice(category: NoticeCategory) {
        CoroutineScope(Dispatchers.IO).launch {
            fetchTopThreeNoticeUseCase.invoke(category)
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            when (category) {
                                NoticeCategory.GENERAL -> {
                                    updateState(
                                        updatedNotificationGeneral = listOf(it.notice1!!, it.notice2!!, it.notice3!!)
                                    )
                                }
                                NoticeCategory.ACADEMIC -> {
                                    updateState(
                                        updatedNotificationAcademic = listOf(it.notice1!!, it.notice2!!, it.notice3!!)
                                    )
                                }
                                NoticeCategory.SCHOLARSHIP -> {
                                    updateState(
                                        updatedNotificationScholarship = listOf(it.notice1!!, it.notice2!!, it.notice3!!)
                                    )
                                }
                                NoticeCategory.EVENT -> {
                                    updateState(
                                        updatedNotificationEvent = listOf(it.notice1!!, it.notice2!!, it.notice3!!)
                                    )
                                }
                            }
                        },
                        onFailure =  {
                            Log.d(fileName, "Retrofit2: Failure: ${it.toString()}")
                        }
                    )
                }
        }
    }

}

data class CategorizedNotificationState(
    val isDisplayed: Boolean = false,
    val notificationGeneral: List<Notice> = listOf(),
    val notificationAcademic: List<Notice> = listOf(),
    val notificationScholarship: List<Notice> = listOf(),
    val notificationEvent: List<Notice> = listOf()
)