package com.doyoonkim.knutice.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.domain.FetchTopThreeNoticeByCategory
import com.doyoonkim.knutice.domain.translate.TextTranslator
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.model.NoticeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
    private val fetchTopThreeNoticeUseCase: FetchTopThreeNoticeByCategory,
    private val translator: TextTranslator
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
                            NoticeCategory.GENERAL_NEWS -> {

                                updateState(
                                    updatedNotificationGeneral = notices
                                )
                            }
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

    fun translate(category: NoticeCategory) {
        Log.d("Test", "Translation Queried")
        viewModelScope.launch {
            val notices = uiState.value.notificationGeneral

            val translated = mutableListOf<Notice>()

            Log.d("Test", "Translation Started")
            notices.forEach { notice ->
                var translatedTitle = notice.title
                var translatedDept = notice.departName

                var updated = notice

                val title = async { translator.translateTo(notice.title)
                    .addOnSuccessListener {
                        Log.d("Test", "$it")
                        translatedTitle = it
                        updated = updated.copy(title = it)
                    }
                    .addOnFailureListener { Log.d("Test", "Unable: ${it.message}") }
                }

                translator.translateTo(notice.departName)
                    .addOnSuccessListener {
                        translatedDept = it
                        updated = updated.copy(departName = it)
                    }
                    .addOnFailureListener { Log.d("Test", "Unable: ${it.message}") }
//                Log.d("Test", updated.toString())
                translated.add(notice.copy(
                    title = translatedTitle,
                    departName = translatedDept
                ).also { Log.d("Test", it.toString()) })
            }

            when(category) {
                NoticeCategory.GENERAL_NEWS -> { _uiState.update { it.copy(notificationGeneral = translated).also { Log.d("Test", it.notificationGeneral.toString()) } } }
                NoticeCategory.ACADEMIC_NEWS -> { _uiState.update { it.copy(notificationAcademic = translated) } }
                NoticeCategory.SCHOLARSHIP_NEWS -> { _uiState.update { it.copy(notificationScholarship = translated) } }
                NoticeCategory.EVENT_NEWS -> { _uiState.update { it.copy(notificationEvent = translated) } }
                else -> {  }
            }
        }
    }
}

data class CategorizedNotificationState(
    val notificationGeneral: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationAcademic: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationScholarship: List<Notice> = listOf(Notice(), Notice(), Notice()),
    val notificationEvent: List<Notice> = listOf(Notice(), Notice(), Notice())
)