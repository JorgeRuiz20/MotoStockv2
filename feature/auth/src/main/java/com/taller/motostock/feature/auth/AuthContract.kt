package com.taller.motostock.feature.auth

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.UserRole

interface AuthContract {
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
        val role: UserRole? = null,
        val isRegisterSuccess: Boolean = false
    ) : UiState

    sealed interface Intent : UiIntent {
        data class Login(val email: String, val pass: String) : Intent
        data class LoginGoogle(val token: String) : Intent
        data object CargarRol : Intent
        data class RegistrarCliente(val email: String, val pass: String, val nombre: String) : Intent
        data class CrearTrabajador(val email: String, val pass: String, val nombre: String) : Intent
        data object ResetRegisterState : Intent
        data object Logout : Intent
    }

    sealed interface Effect : UiEffect {
        data class NavigateTo(val role: UserRole) : Effect
        data class ShowMessage(val message: String) : Effect
        data object NavigateToLogin : Effect
    }
}
