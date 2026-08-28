package com.taller.motostock.feature.history.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.usecase.appointment.GetCitasUseCase
import com.taller.motostock.feature.history.AtencionesHistorialContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private sealed interface HistoryFilter {
    data object All : HistoryFilter
    data class ByPlate(val plate: String) : HistoryFilter
    data class ByDate(val start: Long, val end: Long) : HistoryFilter
}

@HiltViewModel
class AtencionesHistorialViewModel @Inject constructor(
    private val getCitas: GetCitasUseCase
) : MviViewModel<AtencionesHistorialContract.State, AtencionesHistorialContract.Intent, AtencionesHistorialContract.Effect>(
    initialState = AtencionesHistorialContract.State()
) {
    private val filter = MutableStateFlow<HistoryFilter>(HistoryFilter.All)

    init {
        observeAtenciones()
    }

    private fun observeAtenciones() {
        filter.flatMapLatest { currentFilter ->
            when (currentFilter) {
                HistoryFilter.All -> getCitas.getFinalizados()
                is HistoryFilter.ByPlate -> getCitas.buscarPorPlaca(currentFilter.plate)
                is HistoryFilter.ByDate -> getCitas.getPorFecha(currentFilter.start, currentFilter.end)
            }
        }
        .onStart { updateState { copy(isLoading = true) } }
        .onEach { list ->
            updateState { copy(atenciones = list, isLoading = false, error = null) }
        }
        .catch { e ->
            updateState { copy(isLoading = false, error = e.message) }
            sendEffect(AtencionesHistorialContract.Effect.ShowMessage(e.message ?: "Error desconocido"))
        }
        .launchIn(viewModelScope)
    }

    override fun onIntent(intent: AtencionesHistorialContract.Intent) {
        when (intent) {
            is AtencionesHistorialContract.Intent.BuscarPorPlaca -> {
                val plate = intent.placa.trim().uppercase()
                filter.value = if (plate.isNotEmpty()) HistoryFilter.ByPlate(plate) else HistoryFilter.All
            }
            is AtencionesHistorialContract.Intent.FiltrarPorFecha -> {
                filter.value = HistoryFilter.ByDate(intent.inicio, intent.fin)
            }
            AtencionesHistorialContract.Intent.LimpiarFiltro -> {
                filter.value = HistoryFilter.All
            }
        }
    }
}
