package com.doyoonkim.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.usecases.FetchTopicSubscriptionStatusImpl
import com.doyoonkim.domain.usecases.SubmitNotificationPreferencesImpl
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationPreferencesViewModel @Inject constructor(
    private val submitNotificationPreferences: SubmitNotificationPreferencesImpl,
    private val fetchTopicSubscriptionStatus: FetchTopicSubscriptionStatusImpl
) : ViewModel() {

    private var _uiState = MutableStateFlow(NotificationPreferencesState())
    val uiState = _uiState.asStateFlow()

    private val notificationChannels = hashMapOf(
        0 to NoticeCategory.GENERAL_NEWS,
        1 to NoticeCategory.ACADEMIC_NEWS,
        2 to NoticeCategory.SCHOLARSHIP_NEWS,
        3 to NoticeCategory.EVENT_NEWS
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
                isEachChannelAllowed = it.isEachChannelAllowed.updateValueByIndex(index, state)
            )
        }

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
                                isEachChannelAllowed = it.isEachChannelAllowed.updateValueByIndex(index, !state),
                                isError = true
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
                .flowOn(Dispatchers.IO)
                .collectLatest { status ->
                    _uiState.update {
                        it.copy(
                            isEachChannelAllowed = listOf(
                                status.general,
                                status.academic,
                                status.scholarship,
                                status.event
                            )
                        )
                    }
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
    val isEachChannelAllowed: List<Boolean> = listOf(false, false, false, false),
    val isSyncCompleted: Boolean = false,
    val isError: Boolean = false
)