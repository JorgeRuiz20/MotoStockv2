package com.taller.motostock.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.taller.motostock.core.database.MotoStockDb
import com.taller.motostock.core.database.entity.CitaEntity
import com.taller.motostock.core.database.entity.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CitaDao {
    fun getAll(): Flow<List<CitaEntity>>
    fun getPendientes(): Flow<List<CitaEntity>>
    fun getAceptadas(): Flow<List<CitaEntity>>
    fun getEnProceso(): Flow<List<CitaEntity>>
    fun getAceptadasYEnProceso(): Flow<List<CitaEntity>>
    fun getEnTaller(): Flow<List<CitaEntity>>
    fun getFinalizados(): Flow<List<CitaEntity>>
    fun buscarFinalizadosPorPlaca(placa: String): Flow<List<CitaEntity>>
    fun getFinalizadosPorFecha(inicio: Long, fin: Long): Flow<List<CitaEntity>>
    fun getCitasPorCliente(uid: String): Flow<List<CitaEntity>>
    fun getCitasPorEmail(email: String): Flow<List<CitaEntity>>
    fun getCitasPorClienteOEmail(uid: String, email: String): Flow<List<CitaEntity>>
    suspend fun insert(cita: CitaEntity)
    suspend fun insertAll(citas: List<CitaEntity>)
    suspend fun replaceAll(citas: List<CitaEntity>)
    suspend fun update(cita: CitaEntity)
    suspend fun delete(id: String)
}

class SqlDelightCitaDao(private val db: MotoStockDb) : CitaDao {
    private val queries = db.citaQueries

    override fun getAll(): Flow<List<CitaEntity>> =
        queries.getAll().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getPendientes(): Flow<List<CitaEntity>> =
        queries.getPendientes().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getAceptadas(): Flow<List<CitaEntity>> =
        queries.getAceptadas().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getEnProceso(): Flow<List<CitaEntity>> =
        queries.getEnProceso().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getAceptadasYEnProceso(): Flow<List<CitaEntity>> =
        queries.getAceptadasYEnProceso().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getEnTaller(): Flow<List<CitaEntity>> =
        queries.getEnTaller().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getFinalizados(): Flow<List<CitaEntity>> =
        queries.getFinalizados().asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun buscarFinalizadosPorPlaca(placa: String): Flow<List<CitaEntity>> =
        queries.buscarFinalizadosPorPlaca(placa).asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getFinalizadosPorFecha(inicio: Long, fin: Long): Flow<List<CitaEntity>> =
        queries.getFinalizadosPorFecha(inicio, fin).asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getCitasPorCliente(uid: String): Flow<List<CitaEntity>> =
        queries.getCitasPorCliente(uid).asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getCitasPorEmail(email: String): Flow<List<CitaEntity>> =
        queries.getCitasPorEmail(email).asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override fun getCitasPorClienteOEmail(uid: String, email: String): Flow<List<CitaEntity>> =
        queries.getCitasPorClienteOEmail(uid, email).asFlow().mapToList(Dispatchers.Default).map { it.map { r -> r.toEntity() } }

    override suspend fun insert(cita: CitaEntity) {
        queries.insert(
            id = cita.id,
            placa = cita.placa,
            propietario = cita.propietario,
            telefono = cita.telefono,
            modelo = cita.modelo,
            fechaIngreso = cita.fechaIngreso,
            horaIngreso = cita.horaIngreso,
            horaDeseada = cita.horaDeseada,
            tipoServicio = cita.tipoServicio,
            descripcion = cita.descripcion,
            estado = cita.estado,
            fechaSalida = cita.fechaSalida,
            horaSalida = cita.horaSalida,
            motivoRechazo = cita.motivoRechazo,
            motivoCancelacion = cita.motivoCancelacion,
            clienteUid = cita.clienteUid,
            clienteEmail = cita.clienteEmail,
            repuestosUsadosJson = cita.repuestosUsadosJson,
            costoServicio = cita.costoServicio
        )
    }

    override suspend fun insertAll(citas: List<CitaEntity>) {
        db.transaction {
            citas.forEach { cita ->
                queries.insert(
                    id = cita.id,
                    placa = cita.placa,
                    propietario = cita.propietario,
                    telefono = cita.telefono,
                    modelo = cita.modelo,
                    fechaIngreso = cita.fechaIngreso,
                    horaIngreso = cita.horaIngreso,
                    horaDeseada = cita.horaDeseada,
                    tipoServicio = cita.tipoServicio,
                    descripcion = cita.descripcion,
                    estado = cita.estado,
                    fechaSalida = cita.fechaSalida,
                    horaSalida = cita.horaSalida,
                    motivoRechazo = cita.motivoRechazo,
                    motivoCancelacion = cita.motivoCancelacion,
                    clienteUid = cita.clienteUid,
                    clienteEmail = cita.clienteEmail,
                    repuestosUsadosJson = cita.repuestosUsadosJson,
                    costoServicio = cita.costoServicio
                )
            }
        }
    }

    override suspend fun replaceAll(citas: List<CitaEntity>) {
        db.transaction {
            queries.deleteAll()
            citas.forEach { cita ->
                queries.insert(
                    id = cita.id,
                    placa = cita.placa,
                    propietario = cita.propietario,
                    telefono = cita.telefono,
                    modelo = cita.modelo,
                    fechaIngreso = cita.fechaIngreso,
                    horaIngreso = cita.horaIngreso,
                    horaDeseada = cita.horaDeseada,
                    tipoServicio = cita.tipoServicio,
                    descripcion = cita.descripcion,
                    estado = cita.estado,
                    fechaSalida = cita.fechaSalida,
                    horaSalida = cita.horaSalida,
                    motivoRechazo = cita.motivoRechazo,
                    motivoCancelacion = cita.motivoCancelacion,
                    clienteUid = cita.clienteUid,
                    clienteEmail = cita.clienteEmail,
                    repuestosUsadosJson = cita.repuestosUsadosJson,
                    costoServicio = cita.costoServicio
                )
            }
        }
    }

    override suspend fun update(cita: CitaEntity) {
        insert(cita)
    }

    override suspend fun delete(id: String) {
        queries.delete(id)
    }
}
