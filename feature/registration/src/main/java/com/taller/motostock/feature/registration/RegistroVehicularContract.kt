package com.taller.motostock.feature.registration

import com.taller.motostock.core.common.mvi.UiEffect
import com.taller.motostock.core.common.mvi.UiIntent
import com.taller.motostock.core.common.mvi.UiState
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.Repuesto

interface RegistroVehicularContract {
    data class State(
        val vehiculosEnTaller: List<Cita> = emptyList(),
        val repuestos: List<Repuesto> = emptyList(),
        val isLoading: Boolean = false
    ) : UiState

    sealed interface Intent : UiIntent {
        data class RegistrarIngreso(val cita: Cita) : Intent
        data class Finalizar(val cita: Cita, val productos: List<Pair<Repuesto, Int>>, val costo: Double) : Intent
    }

    sealed interface Effect : UiEffect {
        data class ShowMessage(val message: String) : Effect
    }
}
