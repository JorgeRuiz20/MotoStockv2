package com.taller.motostock.core.common.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Clase base para ViewModels MVI en plataformas Apple / iOS.
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S
) {
    val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEffect = Channel<E>(Channel.BUFFERED)
    val uiEffect: Flow<E> = _uiEffect.receiveAsFlow()

    abstract fun onIntent(intent: I)

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update(reducer)
    }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    open fun onCleared() {
        viewModelScope.cancel()
    }
}

