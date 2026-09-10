package com.taller.motostock.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.taller.motostock.core.domain.model.Moto

// DTO de respuesta de la API CarsXE (soporta Perú, gratuita con registro)
// Documentación: https://api.carsxe.com
@Serializable
data class VehicleApiResponse(
    @SerialName("input") val input: String? = null,
    @SerialName("make") val make: String? = null,
    @SerialName("model") val model: String? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("vin") val vin: String? = null,
    @SerialName("engine") val engine: String? = null,
    @SerialName("error") val error: String? = null
)

fun VehicleApiResponse.toDomain(placa: String) = Moto(
    id = placa,
    placa = placa,
    marca = make ?: "Desconocido",
    modelo = model ?: "Desconocido",
    anio = year ?: 0,
    cilindraje = engine ?: "",
    vin = vin ?: ""
)

