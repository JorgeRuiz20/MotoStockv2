package com.taller.motostock.feature.history

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.Cita

interface AtencionesHistorialContract {
    data class State(
        val atenciones: List<Cita> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    ) : UiState

    sealed interface Intent : UiIntent {
        data class BuscarPorPlaca(val placa: String) : Intent
        data class FiltrarPorFecha(val inicio: Long, val fin: Long) : Intent
        data object LimpiarFiltro : Intent
    }

    sealed interface Effect : UiEffect {
        data class ShowMessage(val message: String) : Effect
    }
}
