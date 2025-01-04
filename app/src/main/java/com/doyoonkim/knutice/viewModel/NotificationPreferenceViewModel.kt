package com.doyoonkim.knutice.viewModel

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.data.KnuticeRemoteSource
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.NotificationPreferenceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "notificationPreferences"
)

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

    init {
        viewModelScope.launch {
            // Current Notification Status
            context.dataStore.data
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiStatus.update { status ->
                                // TODO: Consider replace data type with MAP
                                status.copy(
                                    isEachChannelAllowed = listOf(
                                        it[booleanPreferencesKey(NoticeCategory.GENERAL_NEWS.name)] ?: true,
                                        it[booleanPreferencesKey(NoticeCategory.ACADEMIC_NEWS.name)] ?: true,
                                        it[booleanPreferencesKey(NoticeCategory.SCHOLARSHIP_NEWS.name)] ?: true,
                                        it[booleanPreferencesKey(NoticeCategory.EVENT_NEWS.name)] ?: true
                                    )
                                )
                            }
                        },
                        onFailure = {
                            Log.d("DataStore", "Unable to fetch Boolean Preferences")
                        }
                    )
                }
        }
    }

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

    fun updateChannelPreference(id: NoticeCategory, status: Boolean) {
        viewModelScope.launch {
            val channelStatus = List<Boolean>(4) {
                if (it == notificationChannels[id]) status
                else _uiStatus.value.isEachChannelAllowed[it]
            }

            // Local Status (ViewModel)
            launch {
                _uiStatus.update {
                    it.copy(
                        isEachChannelAllowed = channelStatus
                    )
                }
            }

            // Local Data Store
            launch {
                context.dataStore.edit {
                    it[booleanPreferencesKey(id.name)] = status
                }
            }

            // Synchronized with the Preference data on the server.
            withContext(Dispatchers.IO) {
                remoteSource.submitTopicSubscriptionPreference(id, status)
                    .fold(
                        onSuccess = {
                            Log.d("NotificationPreferenceViewModel", "Request Successful, Result: $it")
                        },
                        onFailure = {
                            Log.d("NotificationPreferenceViewModel", "Request Failure, REASON: ${it.message}")
                        }
                    )
            }

        }
    }
}