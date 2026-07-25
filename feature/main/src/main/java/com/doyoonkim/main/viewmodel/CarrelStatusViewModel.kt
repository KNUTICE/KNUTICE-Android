package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doyoonkim.domain.interfaces.AppTokenPreferenceRepository
import com.doyoonkim.domain.interfaces.CarrelStatusRemoteRepository
import com.doyoonkim.domain.interfaces.LocalWidgetCacheRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class CarrelStatusViewModel @Inject constructor(
    private val localWidgetCacheRepository: LocalWidgetCacheRepository,
    private val carrelStatusRepository: CarrelStatusRemoteRepository,
    private val appTokenPreference: AppTokenPreferenceRepository
) : ViewModel() {

    // States
    private val _deviceToken = MutableStateFlow("")
    val deviceToken = _deviceToken.asStateFlow()

    init {
        // Hydrate Device Token
        viewModelScope.launch {
            _deviceToken.update {
                appTokenPreference.getCachedToken() ?: ""
            }
        }
        fetchCarrelRoomStateAndUpdateCache()
    }

    private fun fetchCarrelRoomStateAndUpdateCache() =
        viewModelScope.launch {
            carrelStatusRepository.getCarrelRoomStatus()
                .fold(
                    onSuccess = {
                        Log.d("CarrelStatusViewModel", "Fetch Successful")
                        localWidgetCacheRepository.updateCarrelCache(it)
                    },
                    onFailure = {
                        Log.d("CarrelStatusViewModel", "Unable to fetch status")
                    }
                )
        }
}
