package com.taller.motostock.feature.home

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.UserRole

interface HomeContract {
    data class State(
        val role: UserRole? = null,
        val nombre: String = "",
        val isLoading: Boolean = true,
        // Conteos reales del dashboard
        val citasPendientes: Int = 0,
        val citasEnProceso: Int = 0,
        val citasHoy: Int = 0,
        val repuestosTotal: Int = 0,
        val repuestosStockBajo: Int = 0
    ) : UiState

    sealed interface Intent : UiIntent {
        data object LoadSession : Intent
        data object Logout : Intent
    }

    sealed interface Effect : UiEffect {
        data object NavigateToLogin : Effect
    }
}
