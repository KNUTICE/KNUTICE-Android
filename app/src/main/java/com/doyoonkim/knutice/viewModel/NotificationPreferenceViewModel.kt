package com.doyoonkim.knutice.viewModel

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.data.KnuticeRemoteSource
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.NotificationPreferenceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationPreferenceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteSource: KnuticeRemoteSource
) : ViewModel() {

    private var _uiStatus = MutableStateFlow(NotificationPreferenceStatus())
    val uiStatus = _uiStatus.asStateFlow()

    private val notificationChannels = hashMapOf(
        NoticeCategory.GENERAL_NEWS to 0,
        NoticeCategory.ACADEMIC_NEWS to 1,
        NoticeCategory.SCHOLARSHIP_NEWS to 2,
        NoticeCategory.EVENT_NEWS to 3
    )


    // Needed to be refined later. (If user enters this point without initial permission allowance.
    fun checkMainNotificationPreferenceStatus() {
        val isNotificationAllowed = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        val isMainChannelAllowed = (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .getNotificationChannel(context.getString(R.string.inapp_notification_channel_id))
            .importance > 0

        if (isNotificationAllowed) {
            if (!isMainChannelAllowed) updateMainNotificationStatus(false)
            else updateMainNotificationStatus(true)
        } else {
            updateMainNotificationStatus(false)
        }
    }

    fun updateMainNotificationStatus(status: Boolean) {
        _uiStatus.update {
            it.copy(
                isMainNotificationPermissionGranted = status
            )
        }
    }

    fun checkTopicSubscriptionStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            remoteSource.getTopicSubscriptionStatus()
                .fold(
                    onSuccess = { status ->
                        Log.d("NotificationPreferenceViewModel", "Status: ${status.body?.toString() ?: "empty"}")
                        _uiStatus.update {
                            it.copy(
                                isEachChannelAllowed = listOf(
                                    status.body?.generalNewsTopic ?: false,
                                    status.body?.academicNewsTopic ?: false,
                                    status.body?.scholarshipNewsTopic ?: false,
                                    status.body?.eventNewsTopic ?: false
                                ),
                                isSyncCompleted = true,
                                isError = false
                            )
                        }
                    },
                    onFailure = {
                        Log.d("NotificationPreferenceViewModel", "Unable to update preference status.")
                        _uiStatus.update {
                            it.copy(
                                isSyncCompleted = false,
                                isError = true
                            )
                        }
                    }
                )
        }
    }

    fun updateChannelPreference(id: NoticeCategory, status: Boolean) {
        val updatedStatus = List(_uiStatus.value.isEachChannelAllowed.size) {
            if (it == notificationChannels[id]) status
            else _uiStatus.value.isEachChannelAllowed[it]
        }

        _uiStatus.update {
            it.copy(
                isEachChannelAllowed = updatedStatus
            )
        }

        // Submit updates
        CoroutineScope(Dispatchers.IO).launch {
            val updateJob = launch {
                remoteSource.submitTopicSubscriptionPreference(
                    id, status
                )
            }
            delay(5000L)
            if (!updateJob.isCompleted) updateJob.cancelAndJoin()
        }
    }
}