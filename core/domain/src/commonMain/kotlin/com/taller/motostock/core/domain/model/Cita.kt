package com.taller.motostock.core.domain.model

data class Cita(
    val id: String = "",
    val placa: String = "",
    val propietario: String = "",
    val telefono: String = "",
    val modelo: String = "",
    val fechaIngreso: Long = System.currentTimeMillis(),
    val horaIngreso: String = "",
    val horaDeseada: String = "",
    val tipoServicio: String = "",
    val descripcion: String = "",
    val estado: EstadoCita = EstadoCita.PENDIENTE,
    val fechaSalida: Long? = null,
    val horaSalida: String = "",
    val motivoRechazo: String = "",
    val motivoCancelacion: String = "",
    val clienteUid: String = "",
    val clienteEmail: String = "",
    val repuestosUsadosJson: String = "",
    val costoServicio: Double = 0.0
)

enum class EstadoCita(val label: String) {
    PENDIENTE("Pendiente"),
    ACEPTADA("Aceptada"),
    RECHAZADA("Rechazada"),
    EN_PROCESO("En proceso"),
    CANCELADA("Cancelada"),
    FINALIZADO("Finalizado")
}
