package com.taller.motostock.feature.appointments

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.Repuesto

interface CitasContract {
    data class State(
        val pendientes: List<Cita> = emptyList(),
        val activas: List<Cita> = emptyList(),
        val historial: List<Cita> = emptyList(),
        val repuestos: List<Repuesto> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed interface Intent : UiIntent {
        data class Guardar(val cita: Cita) : Intent
        data class Aceptar(val cita: Cita) : Intent
        data class Rechazar(val cita: Cita, val motivo: String) : Intent
        data class Cancelar(val cita: Cita, val motivo: String) : Intent
        data class PonerEnProceso(val cita: Cita) : Intent
        data class Finalizar(val cita: Cita, val productos: List<Pair<Repuesto, Int>>, val costo: Double) : Intent
        data class BuscarPorPlaca(val placa: String) : Intent
        data class FiltrarPorFecha(val inicio: Long, val fin: Long) : Intent
        data object LimpiarFiltro : Intent
    }

    sealed interface Effect : UiEffect {
        data class ShowMessage(val message: String) : Effect
    }
}
