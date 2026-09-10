package com.taller.motostock.core.database.entity

import com.taller.motostock.core.database.CitaRecord
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.EstadoCita

data class CitaEntity(
    val id: String,
    val placa: String,
    val propietario: String,
    val telefono: String,
    val modelo: String,
    val fechaIngreso: Long,
    val horaIngreso: String,
    val horaDeseada: String = "",
    val tipoServicio: String,
    val descripcion: String,
    val estado: String,
    val fechaSalida: Long? = null,
    val horaSalida: String = "",
    val motivoRechazo: String = "",
    val motivoCancelacion: String = "",
    val clienteUid: String = "",
    val clienteEmail: String = "",
    val repuestosUsadosJson: String = "",
    val costoServicio: Double = 0.0
)

fun CitaEntity.toDomain() = Cita(
    id, placa, propietario, telefono, modelo, fechaIngreso,
    horaIngreso, horaDeseada, tipoServicio, descripcion,
    try { EstadoCita.valueOf(estado) } catch (e: Exception) { EstadoCita.PENDIENTE },
    fechaSalida, horaSalida, motivoRechazo, motivoCancelacion,
    clienteUid, clienteEmail, repuestosUsadosJson, costoServicio
)

fun Cita.toEntity() = CitaEntity(
    id, placa, propietario, telefono, modelo, fechaIngreso,
    horaIngreso, horaDeseada, tipoServicio, descripcion, estado.name,
    fechaSalida, horaSalida, motivoRechazo, motivoCancelacion,
    clienteUid, clienteEmail, repuestosUsadosJson, costoServicio
)

fun CitaRecord.toEntity() = CitaEntity(
    id = id,
    placa = placa,
    propietario = propietario,
    telefono = telefono,
    modelo = modelo,
    fechaIngreso = fechaIngreso,
    horaIngreso = horaIngreso,
    horaDeseada = horaDeseada,
    tipoServicio = tipoServicio,
    descripcion = descripcion,
    estado = estado,
    fechaSalida = fechaSalida,
    horaSalida = horaSalida,
    motivoRechazo = motivoRechazo,
    motivoCancelacion = motivoCancelacion,
    clienteUid = clienteUid,
    clienteEmail = clienteEmail,
    repuestosUsadosJson = repuestosUsadosJson,
    costoServicio = costoServicio
)
