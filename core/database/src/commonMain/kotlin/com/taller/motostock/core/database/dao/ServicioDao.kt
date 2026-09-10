package com.taller.motostock.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.taller.motostock.core.database.MotoStockDb
import com.taller.motostock.core.database.entity.ServicioEntity
import com.taller.motostock.core.database.entity.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface ServicioDao {
    fun getByPlaca(placa: String): Flow<List<ServicioEntity>>
    fun getAll(): Flow<List<ServicioEntity>>
    suspend fun insert(servicio: ServicioEntity)
    suspend fun update(servicio: ServicioEntity)
}

class SqlDelightServicioDao(private val db: MotoStockDb) : ServicioDao {
    private val queries = db.servicioQueries

    override fun getByPlaca(placa: String): Flow<List<ServicioEntity>> =
        queries.getByPlaca(placa).asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getAll(): Flow<List<ServicioEntity>> =
        queries.getAll().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override suspend fun insert(servicio: ServicioEntity) {
        val jsonRepuestos = runCatching { Json.encodeToString(servicio.repuestosUsados) }.getOrDefault("")
        queries.insert(
            id = servicio.id,
            placa = servicio.placa,
            fechaIngreso = servicio.fechaIngreso,
            fechaSalida = servicio.fechaSalida,
            descripcionProblema = servicio.descripcionProblema,
            trabajoRealizado = servicio.trabajoRealizado,
            repuestosUsadosJson = jsonRepuestos,
            kilometraje = servicio.kilometraje.toLong(),
            costoManoObra = servicio.costoManoObra,
            estado = servicio.estado,
            tecnico = servicio.tecnico,
            observaciones = servicio.observaciones
        )
    }

    override suspend fun update(servicio: ServicioEntity) {
        insert(servicio)
    }
}
