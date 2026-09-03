package com.taller.motostock.feature.appointments.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.NotificationHelper
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.EstadoCita
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.usecase.appointment.FinalizarServicioUseCase
import com.taller.motostock.core.domain.usecase.appointment.GetCitasUseCase
import com.taller.motostock.core.domain.usecase.appointment.SaveCitaUseCase
import com.taller.motostock.core.domain.usecase.appointment.UpdateCitaUseCase
import com.taller.motostock.core.domain.usecase.inventory.GetRepuestosUseCase
import com.taller.motostock.feature.appointments.CitasContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private sealed interface HistorialMode {
    data object Todo : HistorialMode
    data class PorPlaca(val placa: String) : HistorialMode
    data class PorFecha(val inicio: Long, val fin: Long) : HistorialMode
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CitasViewModel @Inject constructor(
    private val getCitas: GetCitasUseCase,
    private val saveCita: SaveCitaUseCase,
    private val updateCita: UpdateCitaUseCase,
    private val getRepuestos: GetRepuestosUseCase,
    private val finalizarServicio: FinalizarServicioUseCase,
    private val notificationHelper: NotificationHelper
) : MviViewModel<CitasContract.State, CitasContract.Intent, CitasContract.Effect>(
    initialState = CitasContract.State()
) {

    private val _historialMode = MutableStateFlow<HistorialMode>(HistorialMode.Todo)

    init {
        observeState()
    }

    private fun observeState() {
        combine(
            getCitas.getPendientes(),
            getCitas.getAceptadasYEnProceso(),
            getRepuestos(),
            _historialMode.flatMapLatest { mode ->
                when (mode) {
                    is HistorialMode.Todo      -> getCitas.getFinalizados()
                    is HistorialMode.PorPlaca  -> getCitas.buscarPorPlaca(mode.placa)
                    is HistorialMode.PorFecha  -> getCitas.getPorFecha(mode.inicio, mode.fin)
                }
            }
        ) { pendientes, activas, repuestos, historial ->
            updateState {
                copy(
                    pendientes = pendientes,
                    activas = activas,
                    repuestos = repuestos,
                    historial = historial
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: CitasContract.Intent) {
        when (intent) {
            is CitasContract.Intent.Guardar -> guardar(intent.cita)
            is CitasContract.Intent.Aceptar -> aceptarCita(intent.cita)
            is CitasContract.Intent.Rechazar -> rechazarCita(intent.cita, intent.motivo)
            is CitasContract.Intent.Cancelar -> cancelarCita(intent.cita, intent.motivo)
            is CitasContract.Intent.PonerEnProceso -> ponerEnProceso(intent.cita)
            is CitasContract.Intent.Finalizar -> finalizar(intent.cita, intent.productos, intent.costo)
            is CitasContract.Intent.BuscarPorPlaca -> _historialMode.value = if (intent.placa.isBlank()) HistorialMode.Todo else HistorialMode.PorPlaca(intent.placa.trim().uppercase())
            is CitasContract.Intent.FiltrarPorFecha -> _historialMode.value = HistorialMode.PorFecha(intent.inicio, intent.fin)
            CitasContract.Intent.LimpiarFiltro -> _historialMode.value = HistorialMode.Todo
        }
    }

    private fun guardar(cita: Cita) {
        viewModelScope.launch {
            try {
                saveCita(cita)
                sendEffect(CitasContract.Effect.ShowMessage("Vehículo registrado correctamente"))
            } catch (e: Exception) {
                sendEffect(CitasContract.Effect.ShowMessage(e.message ?: "Error al registrar"))
            }
        }
    }

    private fun aceptarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                val citaActualizada = cita.copy(estado = EstadoCita.ACEPTADA)
                updateCita(citaActualizada)
                sendEffect(CitasContract.Effect.ShowMessage("Cita aceptada"))
                notificationHelper.notificarCitaAceptada(citaActualizada)
            } catch (e: Exception) {
                sendEffect(CitasContract.Effect.ShowMessage(e.message ?: "Error al aceptar"))
            }
        }
    }

    private fun rechazarCita(cita: Cita, motivo: String) {
        viewModelScope.launch {
            try {
                val motivoLimpio = motivo.trim()
                if (motivoLimpio.isBlank()) {
                    sendEffect(CitasContract.Effect.ShowMessage("Indica el motivo del rechazo"))
                    return@launch
                }
                val citaActualizada = cita.copy(estado = EstadoCita.RECHAZADA, motivoRechazo = motivoLimpio)
                updateCita(citaActualizada)
                sendEffect(CitasContract.Effect.ShowMessage("Cita rechazada"))
                notificationHelper.notificarCitaRechazada(citaActualizada)
            } catch (e: Exception) {
                sendEffect(CitasContract.Effect.ShowMessage(e.message ?: "Error al rechazar"))
            }
        }
    }

    private fun cancelarCita(cita: Cita, motivo: String) {
        viewModelScope.launch {
            try {
                val motivoLimpio = motivo.trim()
                if (motivoLimpio.isBlank()) {
                    sendEffect(CitasContract.Effect.ShowMessage("Indica el motivo de la cancelación"))
                    return@launch
                }
                val citaActualizada = cita.copy(estado = EstadoCita.CANCELADA, motivoCancelacion = motivoLimpio)
                updateCita(citaActualizada)
                sendEffect(CitasContract.Effect.ShowMessage("Cita cancelada"))
                notificationHelper.notificarCitaCancelada(citaActualizada)
            } catch (e: Exception) {
                sendEffect(CitasContract.Effect.ShowMessage(e.message ?: "Error al cancelar"))
            }
        }
    }

    private fun ponerEnProceso(cita: Cita) {
        viewModelScope.launch {
            try {
                updateCita(cita.copy(estado = EstadoCita.EN_PROCESO))
                sendEffect(CitasContract.Effect.ShowMessage("Servicio marcado en proceso"))
            } catch (e: Exception) {
                sendEffect(CitasContract.Effect.ShowMessage(e.message ?: "Error"))
            }
        }
    }

    private fun finalizar(cita: Cita, productosSeleccionados: List<Pair<Repuesto, Int>>, costoServicio: Double) {
        viewModelScope.launch {
            try {
                finalizarServicio(cita, productosSeleccionados, costoServicio)
                    .forEach(notificationHelper::notificarStockBajo)

                sendEffect(CitasContract.Effect.ShowMessage("Servicio finalizado"))
            } catch (e: Exception) {
                sendEffect(CitasContract.Effect.ShowMessage(e.message ?: "Error al finalizar"))
            }
        }
    }
}
