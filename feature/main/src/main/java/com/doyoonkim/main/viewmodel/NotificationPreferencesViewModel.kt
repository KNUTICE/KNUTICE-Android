package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchTopicSubscriptionStatus
import com.doyoonkim.domain.usecases.SubmitNotificationPreferences
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationPreferencesViewModel @Inject constructor(
    private val submitNotificationPreferences: SubmitNotificationPreferences,
    private val fetchTopicSubscriptionStatus: FetchTopicSubscriptionStatus
) : ViewModel() {

    private var _uiState = MutableStateFlow(NotificationPreferencesState())
    val uiState = _uiState.asStateFlow()

    private val notificationChannels = hashMapOf(
        0 to NoticeCategory.GENERAL_NEWS,
        1 to NoticeCategory.ACADEMIC_NEWS,
        2 to NoticeCategory.SCHOLARSHIP_NEWS,
        3 to NoticeCategory.EVENT_NEWS,
        4 to NoticeCategory.EMPLOYMENT_NEWS
    )

    fun updateMainNotificationPermissionStatus(status: Boolean) =
        _uiState.update {
            it.copy(
                isMainNotificationPermissionGranted = status
            )
        }

    fun updateChannelPreferenceState(index: Int, state: Boolean) {
        _uiState.update {
            it.copy(
                isSyncCompleted = false
            )
        }

        // TODO: Consider using viewModelScope + Dispatcher Injection in Usecase.
        CoroutineScope(Dispatchers.IO).launch {
            val jobSubmit = launch {
                // Ignore the result.
                submitNotificationPreferences(
                    TopicSubscriptionPreferencesBody(
                        noticeName = notificationChannels[index]!!.name,
                        isSubscribed = state
                    )
                ).collectLatest { result ->
                    if (!result) {
                        _uiState.update {
                            it.copy(
                                isSyncCompleted = true,
                                isError = true
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isEachChannelAllowed = it.isEachChannelAllowed.updateValueByIndex(index, state),
                                isSyncCompleted = true,
                                isError = false
                            )
                        }
                    }
                }
            }
            delay(5000L)
            if (!jobSubmit.isCompleted) jobSubmit.cancelAndJoin()
        }
    }

    fun getTopicSubscriptionStatus() =
        viewModelScope.launch {
            fetchTopicSubscriptionStatus()
                .collectLatest { result ->
                    result.fold(
                        onSuccess =  { status ->
                            _uiState.update {
                                it.copy(
                                    isEachChannelAllowed = listOf(
                                        status.general,
                                        status.academic,
                                        status.scholarship,
                                        status.event,
                                        status.employment
                                    ),
                                    isSyncCompleted = true,
                                    isError = false
                                )
                            }
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(
                                    isSyncCompleted = true,
                                    isError = true
                                )
                            }
                        }
                    )
                }
        }

    private fun List<Boolean>.updateValueByIndex(index: Int, value: Boolean) =
        List(this.size) {
            if (it == index) value
            else this[it]
        }

}

data class NotificationPreferencesState(
    val isMainNotificationPermissionGranted: Boolean = false,
    val isEachChannelAllowed: List<Boolean> = listOf(false, false, false, false, false),
    val isSyncCompleted: Boolean = false,
    val isError: Boolean = false
)