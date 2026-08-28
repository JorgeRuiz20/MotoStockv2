package com.taller.motostock.feature.client.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.NotificationHelper
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.EstadoCita
import com.taller.motostock.core.domain.repository.AuthRepository
import com.taller.motostock.core.domain.repository.CitaRepository
import com.taller.motostock.feature.client.ClienteContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClienteViewModel @Inject constructor(
    private val citaRepository: CitaRepository,
    private val authRepository: AuthRepository,
    private val notificationHelper: NotificationHelper
) : MviViewModel<ClienteContract.State, ClienteContract.Intent, ClienteContract.Effect>(
    initialState = ClienteContract.State()
) {

    init {
        cargarMisCitas()
    }

    override fun onIntent(intent: ClienteContract.Intent) {
        when (intent) {
            is ClienteContract.Intent.AgendarCita -> agendarCita(intent.placa, intent.propietario, intent.telefono, intent.modelo, intent.tipoServicio, intent.descripcion, intent.horaDeseada)
            is ClienteContract.Intent.ReagendarCita -> reagendarCita(intent.citaOriginal, intent.descripcion, intent.horaDeseada)
            is ClienteContract.Intent.ActualizarPlaca -> updateState { copy(placa = intent.placa) }
            ClienteContract.Intent.BuscarCitas -> buscarCitas()
            ClienteContract.Intent.LimpiarPlaca -> updateState { copy(placa = "", searchedCitas = emptyList()) }
        }
    }

    private fun cargarMisCitas() {
        viewModelScope.launch {
            val email = authRepository.usuarioActual() ?: ""
            if (email.isNotBlank()) {
                citaRepository.getCitasPorEmail(email).collect { citas ->
                    updateState { copy(misCitas = citas) }
                }
            }
        }
    }

    private fun buscarCitas() {
        val currentPlaca = uiState.value.placa
        if (currentPlaca.isEmpty()) {
            updateState { copy(error = "Ingrese una placa") }
            return
        }
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            try {
                val citas = citaRepository.getAll()
                    .first()
                    .filter { it.placa.equals(currentPlaca, ignoreCase = true) }
                    .sortedByDescending { it.fechaIngreso }
                updateState { copy(searchedCitas = citas, isLoading = false) }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun agendarCita(
        placa: String,
        propietario: String,
        telefono: String,
        modelo: String,
        tipoServicio: String,
        descripcion: String,
        horaDeseada: String
    ) {
        viewModelScope.launch {
            try {
                val email = authRepository.usuarioActual() ?: ""
                val uid = authRepository.usuarioActualUid().orEmpty()
                val nuevaCita = Cita(
                    id = "",
                    placa = placa.uppercase(),
                    propietario = propietario,
                    telefono = telefono,
                    modelo = modelo,
                    fechaIngreso = System.currentTimeMillis(),
                    horaIngreso = obtenerHoraActual(),
                    horaDeseada = horaDeseada,
                    tipoServicio = tipoServicio,
                    descripcion = descripcion,
                    estado = EstadoCita.PENDIENTE,
                    clienteEmail = email,
                    clienteUid = uid
                )
                citaRepository.save(nuevaCita)
                notificationHelper.notificarNuevaCitaATrabajadores(nuevaCita)
                sendEffect(ClienteContract.Effect.ShowMessage("Cita agendada correctamente"))
            } catch (e: Exception) {
                sendEffect(ClienteContract.Effect.ShowMessage(e.message ?: "Error al agendar"))
            }
        }
    }

    private fun reagendarCita(citaOriginal: Cita, descripcion: String, horaDeseada: String = "") {
        viewModelScope.launch {
            try {
                val email = authRepository.usuarioActual() ?: ""
                val uid = authRepository.usuarioActualUid().orEmpty()
                val nuevaCita = citaOriginal.copy(
                    id = "",
                    fechaIngreso = System.currentTimeMillis(),
                    horaIngreso = obtenerHoraActual(),
                    horaDeseada = horaDeseada,
                    descripcion = descripcion,
                    estado = EstadoCita.PENDIENTE,
                    motivoRechazo = "",
                    motivoCancelacion = "",
                    clienteEmail = email,
                    clienteUid = uid
                )
                citaRepository.save(nuevaCita)
                notificationHelper.notificarNuevaCitaATrabajadores(nuevaCita)
                sendEffect(ClienteContract.Effect.ShowMessage("Cita reagendada correctamente"))
            } catch (e: Exception) {
                sendEffect(ClienteContract.Effect.ShowMessage(e.message ?: "Error al reagendar"))
            }
        }
    }

    private fun obtenerHoraActual(): String {
        val cal = java.util.Calendar.getInstance()
        return String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
    }
}
