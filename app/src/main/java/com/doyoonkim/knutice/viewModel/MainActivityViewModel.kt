package com.doyoonkim.knutice.viewModel

import androidx.lifecycle.ViewModel
import com.doyoonkim.knutice.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    private var _uiState = MutableStateFlow(MainAppState())
    val uiState = _uiState.asStateFlow()

    fun updateState(
        updatedCurrentLocation: Destination = _uiState.value.currentLocation
    ) {
        _uiState.update {
            it.copy(
                currentLocation = updatedCurrentLocation
            )
        }
    }
}

data class MainAppState(
    val currentLocation: Destination = Destination.MAIN
)