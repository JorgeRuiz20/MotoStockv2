package com.taller.motostock.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.taller.motostock.core.common.mvi.MviViewModel
import com.taller.motostock.core.domain.model.UserRole
import com.taller.motostock.core.domain.repository.AuthRepository
import com.taller.motostock.feature.auth.AuthContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : MviViewModel<AuthContract.State, AuthContract.Intent, AuthContract.Effect>(
    initialState = AuthContract.State()
) {

    override fun onIntent(intent: AuthContract.Intent) {
        when (intent) {
            is AuthContract.Intent.Login -> login(intent.email, intent.pass)
            is AuthContract.Intent.LoginGoogle -> loginConGoogle(intent.token)
            AuthContract.Intent.CargarRol -> cargarRolYNavegar()
            is AuthContract.Intent.RegistrarCliente -> registrarCliente(intent.email, intent.pass, intent.nombre)
            is AuthContract.Intent.CrearTrabajador -> crearTrabajador(intent.email, intent.pass, intent.nombre)
            AuthContract.Intent.ResetRegisterState -> updateState { copy(isRegisterSuccess = false, error = null) }
            AuthContract.Intent.Logout -> logout()
        }
    }

    fun haySesionActiva(): Boolean = authRepository.haySesionActiva()

    private fun logout() {
        authRepository.logout()
        updateState { AuthContract.State() }
    }

    private fun cargarRolYNavegar() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            val rol = authRepository.getRolUsuarioActual()
            updateState { copy(isLoading = false, role = rol) }
            sendEffect(AuthContract.Effect.NavigateTo(rol))
        }
    }

    private fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            updateState { copy(error = "Ingresa correo y contraseña") }
            return
        }
        updateState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val resultado = authRepository.login(email.trim(), password)
            if (resultado.isSuccess) {
                val rol = authRepository.getRolUsuarioActual()
                updateState { copy(isLoading = false, role = rol) }
                sendEffect(AuthContract.Effect.NavigateTo(rol))
            } else {
                val errorMsg = mapearError(resultado.exceptionOrNull())
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(AuthContract.Effect.ShowMessage(errorMsg))
            }
        }
    }

    private fun loginConGoogle(idToken: String) {
        updateState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val resultado = authRepository.loginConGoogle(idToken)
            if (resultado.isSuccess) {
                val rol = authRepository.getRolUsuarioActual()
                updateState { copy(isLoading = false, role = rol) }
                sendEffect(AuthContract.Effect.NavigateTo(rol))
            } else {
                val errorMsg = mapearError(resultado.exceptionOrNull())
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(AuthContract.Effect.ShowMessage(errorMsg))
            }
        }
    }

    private fun registrarCliente(email: String, password: String, nombre: String) {
        if (email.isBlank() || password.length < 6) {
            updateState { copy(error = "Correo requerido. Contraseña mínimo 6 caracteres") }
            return
        }
        updateState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val resultado = authRepository.registrarCliente(email.trim(), password, nombre.trim())
            if (resultado.isSuccess) {
                updateState { copy(isLoading = false, isRegisterSuccess = true) }
                sendEffect(AuthContract.Effect.NavigateToLogin)
            } else {
                val errorMsg = mapearError(resultado.exceptionOrNull())
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(AuthContract.Effect.ShowMessage(errorMsg))
            }
        }
    }

    private fun crearTrabajador(email: String, password: String, nombre: String) {
        if (email.isBlank() || password.length < 6) {
            updateState { copy(error = "Correo requerido. Contraseña mínimo 6 caracteres") }
            return
        }
        updateState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val resultado = authRepository.crearTrabajador(email.trim(), password, nombre.trim())
            if (resultado.isSuccess) {
                updateState { copy(isLoading = false, isRegisterSuccess = true) }
                sendEffect(AuthContract.Effect.NavigateToLogin)
            } else {
                val errorMsg = mapearError(resultado.exceptionOrNull())
                updateState { copy(isLoading = false, error = errorMsg) }
                sendEffect(AuthContract.Effect.ShowMessage(errorMsg))
            }
        }
    }

    private fun mapearError(e: Throwable?): String {
        val msg = e?.message ?: return "Error desconocido. Intenta de nuevo"
        return when {
            msg.contains("password is invalid", true) || msg.contains("no user record", true) ->
                "Correo o contraseña incorrectos"
            msg.contains("email address is already in use", true) ->
                "Ese correo ya tiene una cuenta registrada"
            msg.contains("badly formatted", true) -> "El correo no es válido"
            msg.contains("network", true) -> "Sin conexión a internet"
            else -> msg
        }
    }
}
