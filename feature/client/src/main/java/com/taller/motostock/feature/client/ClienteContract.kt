package com.taller.motostock.feature.client

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.Cita

interface ClienteContract {
    data class State(
        val misCitas: List<Cita> = emptyList(),
        val searchedCitas: List<Cita> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val placa: String = ""
    ) : UiState

    sealed interface Intent : UiIntent {
        data class AgendarCita(
            val placa: String,
            val propietario: String,
            val telefono: String,
            val modelo: String,
            val tipoServicio: String,
            val descripcion: String,
            val horaDeseada: String
        ) : Intent
        data class ReagendarCita(val citaOriginal: Cita, val descripcion: String, val horaDeseada: String) : Intent
        data class ActualizarPlaca(val placa: String) : Intent
        data object BuscarCitas : Intent
        data object LimpiarPlaca : Intent
    }

    sealed interface Effect : UiEffect {
        data class ShowMessage(val message: String) : Effect
    }
}
