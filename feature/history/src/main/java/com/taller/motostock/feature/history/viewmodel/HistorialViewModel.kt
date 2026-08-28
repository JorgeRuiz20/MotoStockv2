package com.taller.motostock.feature.history.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.model.Moto
import com.taller.motostock.core.domain.model.ServicioHistorial
import com.taller.motostock.core.domain.usecase.history.BuscarMotoPorPlacaUseCase
import com.taller.motostock.core.domain.usecase.history.GetHistorialPorPlacaUseCase
import com.taller.motostock.core.domain.usecase.history.SaveServicioUseCase
import com.taller.motostock.feature.history.HistorialContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private sealed class BusquedaMode {
    data object Todo : BusquedaMode()
    data class PorPlaca(val placa: String) : BusquedaMode()
    data class PorFecha(val inicio: Long, val fin: Long) : BusquedaMode()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val buscarMoto: BuscarMotoPorPlacaUseCase,
    private val getHistorial: GetHistorialPorPlacaUseCase,
    private val saveServicio: SaveServicioUseCase
) : MviViewModel<HistorialContract.State, HistorialContract.Intent, HistorialContract.Effect>(
    initialState = HistorialContract.State(isLoading = true)
) {

    private val _busqueda = MutableStateFlow<BusquedaMode>(BusquedaMode.Todo)

    init {
        observeBusqueda()
    }

    private fun observeBusqueda() {
        _busqueda
            .flatMapLatest { modo ->
                when (modo) {
                    is BusquedaMode.Todo -> {
                        getHistorial("").map { lista ->
                            HistorialContract.State(servicios = lista)
                        }
                    }
                    is BusquedaMode.PorPlaca -> {
                        flow {
                            emit(HistorialContract.State(isLoading = true))
                            val resultado = buscarMoto(modo.placa)
                            resultado.onSuccess { moto ->
                                emitAll(
                                    getHistorial(modo.placa).map { servicios ->
                                        HistorialContract.State(motoEncontrada = moto, servicios = servicios)
                                    }
                                )
                            }.onFailure {
                                emit(HistorialContract.State(error = "No se encontró la placa"))
                            }
                        }
                    }
                    is BusquedaMode.PorFecha -> {
                        getHistorial("").map { todos ->
                            val filtrados = todos.filter { it.fechaIngreso in modo.inicio..modo.fin }
                            HistorialContract.State(servicios = filtrados)
                        }
                    }
                }
            }
            .onEach { newState -> updateState { newState } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: HistorialContract.Intent) {
        when (intent) {
            is HistorialContract.Intent.BuscarPorPlaca -> _busqueda.value = BusquedaMode.PorPlaca(intent.placa.trim().uppercase())
            HistorialContract.Intent.CargarTodo -> _busqueda.value = BusquedaMode.Todo
            is HistorialContract.Intent.FiltrarPorFecha -> _busqueda.value = BusquedaMode.PorFecha(intent.inicio, intent.fin)
            is HistorialContract.Intent.GuardarServicio -> guardarServicio(intent.servicio)
        }
    }

    private fun guardarServicio(servicio: ServicioHistorial) {
        viewModelScope.launch {
            try {
                saveServicio(servicio)
                sendEffect(HistorialContract.Effect.ShowMessage("Servicio registrado correctamente"))
            } catch (e: Exception) {
                sendEffect(HistorialContract.Effect.ShowMessage(e.message ?: "Error al guardar"))
            }
        }
    }
}
