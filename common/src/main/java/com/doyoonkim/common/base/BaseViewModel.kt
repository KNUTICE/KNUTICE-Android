package com.doyoonkim.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UiState
interface UiEvent
interface UiSideEffect
interface UiMutation

abstract class BaseViewModel<S: UiState, E: UiEvent, SE: UiSideEffect, M: UiMutation>: ViewModel() {
    // Function for initial state set.
    private val initialState by lazy { setInitialState() }
    abstract fun setInitialState(): S

    // set _uiState as protected abstract var would cause "accessing non-final variable" when calling .asStateFlow.
    // MutableStateFlow must be initialized with proper state.
    private val _uiState: MutableStateFlow<S> = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    // Shared Flow to process Event (Intent)
    // Set replay 0 for avoiding duplicated event handling
    // Set bufferCapacity for avoiding infinite suspend on emit function. (let DROP_OLDEST for exceeding Buffer capacity case)
    private val _uiEvent = MutableSharedFlow<E>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val uiEvent = _uiEvent.asSharedFlow()

    // Channel to process SideEffect if necessary.
    private val _uiSideEffect = Channel<SE>()
    val uiSideEffect = _uiSideEffect.receiveAsFlow()

    init {
        receiveEvents()
    }

    /**
     * stateUpdate
     * Function to update current UiState to renewed UiState.
     */
    @Deprecated("Deprecated. Use mutate instead.")
    protected fun stateUpdate(update: (S) -> S) {
        _uiState.update {
            update(it)
        }
    }

    /**
     * mutate
     * Function that enforces to use reducer function to update state.
     */
    protected fun mutate(mutation: M) {
        _uiState.update { currentState ->
            reduce(currentState, mutation)
        }
    }

    /**
     * sendUiEvent
     * Publicly opened function to send UI event based on User Interaction.
     */
    fun sendUiEvent(event: E) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    /**
     * receiveEvents
     * Internally used function to collect Event SharedFlow to process the event.
     * Collecting this function would ensure sequential processing of event.
     */
    private fun receiveEvents() {
        viewModelScope.launch {
            uiEvent.collect { event ->
                handleEvent(event)
            }
        }
    }

    /**
     * sendSideEffect
     * Function to initiate side effect
     */
    protected fun sendSideEffect(sideEffect: SE) {
        viewModelScope.launch { _uiSideEffect.send(sideEffect) }
    }

    /**
     * handleEvent
     * Function to process received event
     */
    protected abstract fun handleEvent(event: E)

    /**
     * reducer
     * Pure Function that mutate the state based on the given mutation.
     */
    protected abstract fun reduce(currentState: S, mutation: M): S
}
