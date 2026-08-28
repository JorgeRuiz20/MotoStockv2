package com.taller.motostock.feature.home

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.UserRole

interface HomeContract {
    data class State(
        val role: UserRole? = null,
        val isLoading: Boolean = true
    ) : UiState

    sealed interface Intent : UiIntent {
        data object LoadSession : Intent
        data object Logout : Intent
    }

    sealed interface Effect : UiEffect {
        data object NavigateToLogin : Effect
    }
}
