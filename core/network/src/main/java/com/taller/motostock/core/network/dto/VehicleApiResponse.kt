package com.taller.motostock.core.network.dto

import com.google.gson.annotations.SerializedName
import com.taller.motostock.core.domain.model.Moto

// DTO de respuesta de la API CarsXE (soporta Perú, gratuita con registro)
// Documentación: https://api.carsxe.com
data class VehicleApiResponse(
    @SerializedName("input") val input: String? = null,
    @SerializedName("make") val make: String? = null,
    @SerializedName("model") val model: String? = null,
    @SerializedName("year") val year: Int? = null,
    @SerializedName("vin") val vin: String? = null,
    @SerializedName("engine") val engine: String? = null,
    @SerializedName("error") val error: String? = null
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
