package com.taller.motostock.feature.auth.viewmodel

import com.taller.motostock.core.domain.repository.AuthRepository
import com.taller.motostock.feature.auth.AuthContract
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class LoginViewModelTest {
    private val repository: AuthRepository = mock()

    @Test
    fun `login con credenciales vacias actualiza el estado con error`() {
        val viewModel = LoginViewModel(repository)
        viewModel.onIntent(AuthContract.Intent.Login(email = "", pass = "123456"))
        assertEquals("Ingresa correo y contraseña", viewModel.uiState.value.error)
    }

    @Test
    fun `registro con contrasena corta actualiza el estado con error`() {
        val viewModel = LoginViewModel(repository)
        viewModel.onIntent(AuthContract.Intent.RegistrarCliente("cliente@moto.com", "123", "Ana"))
        assertEquals("Correo requerido. Contraseña mínimo 6 caracteres", viewModel.uiState.value.error)
    }
}
