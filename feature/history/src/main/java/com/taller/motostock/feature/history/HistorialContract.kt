package com.taller.motostock.feature.history

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.Moto
import com.taller.motostock.core.domain.model.ServicioHistorial

interface HistorialContract {
    data class State(
        val servicios: List<ServicioHistorial> = emptyList(),
        val motoEncontrada: Moto? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed interface Intent : UiIntent {
        data class BuscarPorPlaca(val placa: String) : Intent
        data object CargarTodo : Intent
        data class FiltrarPorFecha(val inicio: Long, val fin: Long) : Intent
        data class GuardarServicio(val servicio: ServicioHistorial) : Intent
    }

    sealed interface Effect : UiEffect {
        data class ShowMessage(val message: String) : Effect
    }
}
