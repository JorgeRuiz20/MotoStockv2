package com.taller.motostock.core.domain.repository

import com.taller.motostock.core.domain.model.UserRole

interface AuthRepository {
    fun usuarioActual(): String?
    fun usuarioActualUid(): String?
    fun haySesionActiva(): Boolean
    suspend fun getRolUsuarioActual(): UserRole
    suspend fun getNombreUsuarioActual(): String
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun loginConGoogle(idToken: String): Result<Unit>
    /** Registro público: siempre crea un CLIENTE. */
    suspend fun registrarCliente(email: String, password: String, nombre: String): Result<Unit>
    /** Solo el ADMINISTRADOR puede llamar esto para crear trabajadores. */
    suspend fun crearTrabajador(email: String, password: String, nombre: String): Result<Unit>
    fun logout()
}
