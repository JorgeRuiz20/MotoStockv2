package com.taller.motostock.core.database.entity

import com.taller.motostock.core.database.RepuestoRecord
import com.taller.motostock.core.domain.model.Repuesto

data class RepuestoEntity(
    val id: String,
    val nombre: String,
    val categoria: String,
    val cantidad: Int,
    val precioCompra: Double,
    val precioVenta: Double,
    val stockMinimo: Int,
    val proveedor: String,
    val fechaActualizacion: Long
)

fun RepuestoEntity.toDomain() = Repuesto(id, nombre, categoria, cantidad, precioCompra, precioVenta, stockMinimo, proveedor, fechaActualizacion)
fun Repuesto.toEntity() = RepuestoEntity(id, nombre, categoria, cantidad, precioCompra, precioVenta, stockMinimo, proveedor, fechaActualizacion)

fun RepuestoRecord.toEntity() = RepuestoEntity(
    id = id,
    nombre = nombre,
    categoria = categoria,
    cantidad = cantidad.toInt(),
    precioCompra = precioCompra,
    precioVenta = precioVenta,
    stockMinimo = stockMinimo.toInt(),
    proveedor = proveedor,
    fechaActualizacion = fechaActualizacion
)
