package com.taller.motostock.core.domain.model

data class Moto(
    val id: String = "",
    val placa: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: Int = 0,
    val color: String = "",
    val propietario: String = "",
    val telefono: String = "",
    val cilindraje: String = "",
    val vin: String = ""
)
