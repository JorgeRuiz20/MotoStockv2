package com.taller.motostock.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Clase base para ViewModels que siguen el patrón MVI.
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEffect = Channel<E>(Channel.BUFFERED)
    val uiEffect: Flow<E> = _uiEffect.receiveAsFlow()

    /**
     * Procesa los intentos del usuario.
     */
    abstract fun onIntent(intent: I)

    /**
     * Actualiza el estado de la UI de forma atómica.
     */
    protected fun updateState(reducer: S.() -> S) {
        _uiState.update(reducer)
    }

    /**
     * Envía un efecto secundario.
     */
    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }
}
