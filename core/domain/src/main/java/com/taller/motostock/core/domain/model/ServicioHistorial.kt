package com.taller.motostock.core.domain.model

data class ServicioHistorial(
    val id: String = "",
    val placa: String = "",
    val fechaIngreso: Long = System.currentTimeMillis(),
    val fechaSalida: Long? = null,
    val descripcionProblema: String = "",
    val trabajoRealizado: String = "",
    val repuestosUsados: List<RepuestoUsado> = emptyList(),
    val kilometraje: Int = 0,
    val costoManoObra: Double = 0.0,
    val estado: EstadoServicio = EstadoServicio.PENDIENTE,
    val tecnico: String = "",
    val observaciones: String = ""
) {
    val costoTotal: Double get() = costoManoObra + repuestosUsados.sumOf { it.subtotal }
}

data class RepuestoUsado(
    val repuestoId: String = "",
    val nombre: String = "",
    val cantidad: Int = 1,
    val precioUnitario: Double = 0.0
) {
    val subtotal: Double get() = cantidad * precioUnitario
}

enum class EstadoServicio(val label: String) {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    LISTO("Listo"),
    ENTREGADO("Entregado")
}
