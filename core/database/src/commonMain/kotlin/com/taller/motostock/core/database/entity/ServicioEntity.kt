package com.taller.motostock.core.database.entity

import com.taller.motostock.core.database.ServicioRecord
import com.taller.motostock.core.domain.model.EstadoServicio
import com.taller.motostock.core.domain.model.RepuestoUsado
import com.taller.motostock.core.domain.model.ServicioHistorial
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class ServicioEntity(
    val id: String,
    val placa: String,
    val fechaIngreso: Long,
    val fechaSalida: Long?,
    val descripcionProblema: String,
    val trabajoRealizado: String,
    val repuestosUsados: List<RepuestoUsado>,
    val kilometraje: Int,
    val costoManoObra: Double,
    val estado: String,
    val tecnico: String,
    val observaciones: String
)

fun ServicioEntity.toDomain() = ServicioHistorial(
    id, placa, fechaIngreso, fechaSalida, descripcionProblema,
    trabajoRealizado, repuestosUsados, kilometraje, costoManoObra,
    EstadoServicio.valueOf(estado), tecnico, observaciones
)

fun ServicioHistorial.toEntity() = ServicioEntity(
    id, placa, fechaIngreso, fechaSalida, descripcionProblema,
    trabajoRealizado, repuestosUsados, kilometraje, costoManoObra,
    estado.name, tecnico, observaciones
)

fun ServicioRecord.toEntity(): ServicioEntity {
    val repuestos: List<RepuestoUsado> = runCatching {
        if (repuestosUsadosJson.isBlank()) emptyList()
        else Json.decodeFromString<List<RepuestoUsado>>(repuestosUsadosJson)
    }.getOrDefault(emptyList())

    return ServicioEntity(
        id = id,
        placa = placa,
        fechaIngreso = fechaIngreso,
        fechaSalida = fechaSalida,
        descripcionProblema = descripcionProblema,
        trabajoRealizado = trabajoRealizado,
        repuestosUsados = repuestos,
        kilometraje = kilometraje.toInt(),
        costoManoObra = costoManoObra,
        estado = estado,
        tecnico = tecnico,
        observaciones = observaciones
    )
}
