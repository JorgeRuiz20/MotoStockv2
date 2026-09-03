package com.taller.motostock.core.domain.repository

import com.taller.motostock.core.domain.model.Cita
import kotlinx.coroutines.flow.Flow

interface CitaRepository {
    fun getAll(): Flow<List<Cita>>
    fun getPendientes(): Flow<List<Cita>>
    fun getAceptadas(): Flow<List<Cita>>
    fun getEnProceso(): Flow<List<Cita>>
    fun getAceptadasYEnProceso(): Flow<List<Cita>>
    fun getEnTaller(): Flow<List<Cita>>
    fun getFinalizados(): Flow<List<Cita>>
    fun buscarFinalizadosPorPlaca(placa: String): Flow<List<Cita>>
    fun getFinalizadosPorFecha(inicio: Long, fin: Long): Flow<List<Cita>>
    fun getCitasPorCliente(uid: String): Flow<List<Cita>>
    fun getCitasPorEmail(email: String): Flow<List<Cita>>
    fun getCitasPorClienteOEmail(uid: String, email: String): Flow<List<Cita>>
    suspend fun save(cita: Cita)
    suspend fun update(cita: Cita)
    suspend fun delete(id: String)
}
