package com.taller.motostock.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.taller.motostock.core.domain.model.EstadoServicio
import com.taller.motostock.core.domain.model.RepuestoUsado
import com.taller.motostock.core.domain.model.ServicioHistorial

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromRepuestoList(value: List<RepuestoUsado>): String = gson.toJson(value)

    @TypeConverter
    fun toRepuestoList(value: String): List<RepuestoUsado> {
        val type = object : TypeToken<List<RepuestoUsado>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}

@Entity(tableName = "servicios")
@TypeConverters(Converters::class)
data class ServicioEntity(
    @PrimaryKey val id: String,
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
