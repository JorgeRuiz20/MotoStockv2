package com.taller.motostock.feature.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.repository.AuthRepository
import com.taller.motostock.core.domain.repository.CitaRepository
import com.taller.motostock.core.domain.repository.RepuestoRepository
import com.taller.motostock.feature.home.HomeContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val citaRepository: CitaRepository,
    private val repuestoRepository: RepuestoRepository
) : MviViewModel<HomeContract.State, HomeContract.Intent, HomeContract.Effect>(
    initialState = HomeContract.State()
) {

    init {
        onIntent(HomeContract.Intent.LoadSession)
    }

    override fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            HomeContract.Intent.LoadSession -> loadSession()
            HomeContract.Intent.Logout -> logout()
        }
    }

    private fun loadSession() {
        viewModelScope.launch {
            val role = authRepository.getRolUsuarioActual()
            val nombre = authRepository.getNombreUsuarioActual()
                .takeIf { it.isNotBlank() }
                ?: (authRepository.usuarioActual() ?: "")
            updateState { copy(role = role, nombre = nombre, isLoading = false) }
        }

        // Observar citas en tiempo real para el dashboard
        combine(
            citaRepository.getPendientes(),
            citaRepository.getAceptadasYEnProceso(),
            repuestoRepository.getAll(),
            repuestoRepository.getStockBajo()
        ) { pendientes, activas, repuestos, stockBajo ->
            updateState {
                copy(
                    citasPendientes = pendientes.size,
                    citasEnProceso = activas.size,
                    citasHoy = pendientes.size + activas.size,
                    repuestosTotal = repuestos.size,
                    repuestosStockBajo = stockBajo.size
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun logout() {
        authRepository.logout()
        sendEffect(HomeContract.Effect.NavigateToLogin)
    }
}
