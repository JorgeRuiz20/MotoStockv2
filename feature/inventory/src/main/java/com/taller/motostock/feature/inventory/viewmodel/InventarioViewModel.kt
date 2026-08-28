package com.taller.motostock.feature.inventory.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.usecase.inventory.DeleteRepuestoUseCase
import com.taller.motostock.core.domain.usecase.inventory.GetRepuestosUseCase
import com.taller.motostock.core.domain.usecase.inventory.SaveRepuestoUseCase
import com.taller.motostock.feature.inventory.InventarioContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val getRepuestos: GetRepuestosUseCase,
    private val saveRepuesto: SaveRepuestoUseCase,
    private val deleteRepuesto: DeleteRepuestoUseCase
) : MviViewModel<InventarioContract.State, InventarioContract.Intent, InventarioContract.Effect>(
    initialState = InventarioContract.State()
) {

    init {
        cargarRepuestos()
        cargarStockBajo()
    }

    override fun onIntent(intent: InventarioContract.Intent) {
        when (intent) {
            is InventarioContract.Intent.Guardar -> guardar(intent.repuesto)
            is InventarioContract.Intent.Eliminar -> eliminar(intent.id)
            InventarioContract.Intent.ClearError -> updateState { copy(error = null) }
        }
    }

    private fun cargarRepuestos() {
        updateState { copy(isLoading = true) }
        getRepuestos()
            .onEach { lista -> updateState { copy(repuestos = lista, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    private fun cargarStockBajo() {
        getRepuestos.stockBajo()
            .onEach { lista -> updateState { copy(stockBajo = lista) } }
            .launchIn(viewModelScope)
    }

    private fun guardar(repuesto: Repuesto) {
        viewModelScope.launch {
            updateState { copy(isSaving = true) }
            try {
                saveRepuesto(repuesto)
                val msg = if (repuesto.id.isEmpty()) "Repuesto agregado" else "Repuesto actualizado"
                sendEffect(InventarioContract.Effect.ShowMessage(msg))
                sendEffect(InventarioContract.Effect.NavigateBack)
            } catch (e: Exception) {
                updateState { copy(error = e.message) }
                sendEffect(InventarioContract.Effect.ShowMessage("Error al guardar: ${e.message}"))
            } finally {
                updateState { copy(isSaving = false) }
            }
        }
    }

    private fun eliminar(id: String) {
        viewModelScope.launch {
            try {
                deleteRepuesto(id)
                sendEffect(InventarioContract.Effect.ShowMessage("Repuesto eliminado"))
            } catch (e: Exception) {
                sendEffect(InventarioContract.Effect.ShowMessage("Error al eliminar: ${e.message}"))
            }
        }
    }
}
