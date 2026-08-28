package com.taller.motostock.feature.registration.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.NotificationHelper
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.usecase.appointment.FinalizarServicioUseCase
import com.taller.motostock.core.domain.usecase.appointment.GetCitasUseCase
import com.taller.motostock.core.domain.usecase.appointment.SaveCitaUseCase
import com.taller.motostock.core.domain.usecase.inventory.GetRepuestosUseCase
import com.taller.motostock.feature.registration.RegistroVehicularContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroVehicularViewModel @Inject constructor(
    private val getCitas: GetCitasUseCase,
    private val getRepuestos: GetRepuestosUseCase,
    private val saveCita: SaveCitaUseCase,
    private val finalizarServicio: FinalizarServicioUseCase,
    private val notificationHelper: NotificationHelper
) : MviViewModel<RegistroVehicularContract.State, RegistroVehicularContract.Intent, RegistroVehicularContract.Effect>(
    initialState = RegistroVehicularContract.State()
) {
    init {
        observeState()
    }

    private fun observeState() {
        combine(
            getCitas.getEnProceso(), 
            getRepuestos()
        ) { citas, repuestos ->
            updateState { copy(vehiculosEnTaller = citas, repuestos = repuestos) }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: RegistroVehicularContract.Intent) {
        when (intent) {
            is RegistroVehicularContract.Intent.RegistrarIngreso -> save(intent.cita)
            is RegistroVehicularContract.Intent.Finalizar -> finish(intent.cita, intent.productos, intent.costo)
        }
    }

    private fun save(cita: Cita) = viewModelScope.launch {
        runCatching { saveCita(cita) }
            .onSuccess { sendEffect(RegistroVehicularContract.Effect.ShowMessage("Vehículo registrado correctamente")) }
            .onFailure { sendEffect(RegistroVehicularContract.Effect.ShowMessage(it.message ?: "No se pudo registrar el vehículo")) }
    }

    private fun finish(cita: Cita, products: List<Pair<Repuesto, Int>>, cost: Double) = viewModelScope.launch {
        runCatching { finalizarServicio(cita, products, cost) }
            .onSuccess {
                it.forEach(notificationHelper::notificarStockBajo)
                sendEffect(RegistroVehicularContract.Effect.ShowMessage("Servicio finalizado"))
            }
            .onFailure { sendEffect(RegistroVehicularContract.Effect.ShowMessage(it.message ?: "No se pudo finalizar el servicio")) }
    }
}
