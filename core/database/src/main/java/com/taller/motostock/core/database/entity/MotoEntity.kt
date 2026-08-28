package com.taller.motostock.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taller.motostock.core.domain.model.Moto

@Entity(tableName = "motos")
data class MotoEntity(
    @PrimaryKey val id: String,
    val placa: String,
    val marca: String,
    val modelo: String,
    val anio: Int,
    val color: String,
    val propietario: String,
    val telefono: String,
    val cilindraje: String,
    val vin: String
)

fun MotoEntity.toDomain() = Moto(id, placa, marca, modelo, anio, color, propietario, telefono, cilindraje, vin)
fun Moto.toEntity() = MotoEntity(id.ifEmpty { placa }, placa, marca, modelo, anio, color, propietario, telefono, cilindraje, vin)
