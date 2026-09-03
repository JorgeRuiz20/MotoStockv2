package com.taller.motostock.feature.admin.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.usecase.auth.CreateWorkerUseCase
import com.taller.motostock.feature.admin.AdminContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val createWorker: CreateWorkerUseCase
) : MviViewModel<AdminContract.State, AdminContract.Intent, AdminContract.Effect>(
    initialState = AdminContract.State()
) {

    override fun onIntent(intent: AdminContract.Intent) {
        when (intent) {
            is AdminContract.Intent.CrearTrabajador -> createWorker(intent)
            AdminContract.Intent.ClearError -> updateState { copy(error = null) }
        }
    }

    private fun createWorker(intent: AdminContract.Intent.CrearTrabajador) = viewModelScope.launch {
        if (intent.nombre.isBlank() || intent.email.isBlank() || intent.password.length < 6) {
            updateState { copy(error = "Completa los datos; la contraseña debe tener al menos 6 caracteres.") }
            return@launch
        }
        updateState { copy(isLoading = true, error = null) }
        val result = createWorker(intent.email.trim(), intent.password, intent.nombre.trim())
        if (result.isSuccess) {
            updateState { AdminContract.State() }
            sendEffect(AdminContract.Effect.NavigateBackToDashboard)
        } else {
            val message = result.exceptionOrNull()?.message ?: "No se pudo crear el trabajador"
            updateState { copy(isLoading = false, error = message) }
            sendEffect(AdminContract.Effect.ShowMessage(message))
        }
    }
}
