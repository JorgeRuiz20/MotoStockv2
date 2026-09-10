package com.taller.motostock.core.domain.model

data class Repuesto(
    val id: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val cantidad: Int = 0,
    val precioCompra: Double = 0.0,
    val precioVenta: Double = 0.0,
    val stockMinimo: Int = 5,
    val proveedor: String = "",
    val fechaActualizacion: Long = System.currentTimeMillis()
) {
    val stockBajo: Boolean get() = cantidad <= stockMinimo
}
