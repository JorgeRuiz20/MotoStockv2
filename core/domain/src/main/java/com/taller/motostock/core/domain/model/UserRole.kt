package com.taller.motostock.core.domain.model

enum class UserRole(val value: String, val displayName: String) {
    CLIENTE("cliente", "Cliente"),
    TRABAJADOR("trabajador", "Trabajador"),
    ADMINISTRADOR("administrador", "Administrador");

    companion object {
        fun fromValue(value: String): UserRole =
            entries.firstOrNull { it.value == value } ?: CLIENTE
    }
}

data class Usuario(
    val uid: String = "",
    val email: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val role: UserRole = UserRole.CLIENTE,
    val creadoEn: Long = System.currentTimeMillis()
)
