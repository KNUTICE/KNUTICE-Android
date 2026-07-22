package com.doyoonkim.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @author kimdoyoon
 * Created 7/10/26 at 8:55 PM
 */
interface ViewModelState<S : UiState> {
    fun toUiState(): S
}

abstract class KNBaseViewModel<
        VS : ViewModelState<S>, S : UiState, E : UiEvent, SE : UiSideEffect, M : UiMutation>() :
    ViewModel() {

    companion object {
        private const val TAG = "KNBaseViewModel"
    }

    private val initialViewModelState by lazy { setInitialViewModelState() }
    abstract fun setInitialViewModelState(): VS

    private val _viewModelState: MutableStateFlow<VS> = MutableStateFlow<VS>(initialViewModelState)
    protected val viewModelState = _viewModelState.asStateFlow()

    /**
     * Exposed [StateFlow] for UI.
     * ViewModelFlow would be transformed to collectable state flow of [UiState]
     */
    val uiState: StateFlow<S> = _viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    /**
     * Shared Flow to process [UiEvent].
     * Set reply 0 for avoiding duplicated [UiEvent] handling
     * Set bufferCapacity for avoiding infinite suspend on emit function.
     * (Let DROP_OLDEST for exceeding Buffer)
     */
    private val _uiEvent = MutableSharedFlow<E>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val uiEvent = _uiEvent.asSharedFlow()

    /**
     * [Channel] to process [UiSideEffect[ if necessary.
     */
    private val _uiSideEffect = Channel<SE>()
    val uiSideEffect = _uiSideEffect.receiveAsFlow()

    init {
        receiveEvent()
    }

    /**
     * Function that process requested [UiMutation]
     * Internally calls reduce function with [UiMutation] to generate new [ViewModelState]
     */
    protected fun mutate(mutation: M) {
        _viewModelState.update { currentState ->
            reduce(currentState, mutation)
        }
    }

    /**
     * Publicly accessible function to send [UiEvent] based on User Interaction.
     */
    fun sendUiEvent(event: E) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    /**
     * Internally used function to collect [kotlinx.coroutines.flow.SharedFlow] to process the [UiEvent]
     * Collecting via this function would ensure sequential processing of event
     */
    private fun receiveEvent() {
        viewModelScope.launch {
            uiEvent.collect { event ->
                handleEvent(event)
            }
        }
    }

    /**
     * Internally accessible function to initiate new [UiSideEffect]
     */
    protected fun sendSideEffect(sideEffect: SE) {
        viewModelScope.launch { _uiSideEffect.send(sideEffect) }
    }

    /**
     * Function to process received [UiEvent]
     */
    protected abstract fun handleEvent(event: E)

    /**
     * Pure function that mutate the [ViewModelState] based on the given [UiMutation]
     */
    protected abstract fun reduce(currentState: VS, mutation: M): VS

}