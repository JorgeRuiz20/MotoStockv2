package com.taller.motostock.core.database.entity

import com.taller.motostock.core.database.MotoRecord
import com.taller.motostock.core.domain.model.Moto

data class MotoEntity(
    val id: String,
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

fun MotoRecord.toEntity() = MotoEntity(
    id = id,
    placa = placa,
    marca = marca,
    modelo = modelo,
    anio = anio.toInt(),
    color = color,
    propietario = propietario,
    telefono = telefono,
    cilindraje = cilindraje,
    vin = vin
)
