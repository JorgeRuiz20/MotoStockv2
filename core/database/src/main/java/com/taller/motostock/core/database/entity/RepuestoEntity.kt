package com.taller.motostock.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taller.motostock.core.domain.model.Repuesto

@Entity(tableName = "repuestos")
data class RepuestoEntity(
    @PrimaryKey val id: String,
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
