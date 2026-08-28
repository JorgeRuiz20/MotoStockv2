package com.taller.motostock.feature.inventory

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.Repuesto

interface InventarioContract {
    data class State(
        val repuestos: List<Repuesto> = emptyList(),
        val stockBajo: List<Repuesto> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSaving: Boolean = false
    ) : UiState

    sealed interface Intent : UiIntent {
        data class Guardar(val repuesto: Repuesto) : Intent
        data class Eliminar(val id: String) : Intent
        data object ClearError : Intent
    }

    sealed interface Effect : UiEffect {
        data class ShowMessage(val message: String) : Effect
        data object NavigateBack : Effect
    }
}
