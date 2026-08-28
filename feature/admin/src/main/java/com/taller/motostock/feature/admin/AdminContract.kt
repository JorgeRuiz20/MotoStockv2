package com.taller.motostock.feature.admin

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState

interface AdminContract {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed interface Intent : UiIntent {
        data class CrearTrabajador(val email: String, val password: String, val nombre: String) : Intent
        data object ClearError : Intent
    }

    sealed interface Effect : UiEffect {
        data class ShowMessage(val message: String) : Effect
        data object NavigateToLogin : Effect
    }
}
