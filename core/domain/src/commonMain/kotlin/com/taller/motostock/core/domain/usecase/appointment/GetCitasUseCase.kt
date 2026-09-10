package com.taller.motostock.core.domain.usecase.appointment

import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.repository.CitaRepository
import kotlinx.coroutines.flow.Flow
import com.taller.motostock.core.domain.di.Inject

class GetCitasUseCase @Inject constructor(private val repo: CitaRepository) {
    fun getAll(): Flow<List<Cita>> = repo.getAll()
    fun getPendientes(): Flow<List<Cita>> = repo.getPendientes()
    fun getAceptadas(): Flow<List<Cita>> = repo.getAceptadas()
    fun getEnProceso(): Flow<List<Cita>> = repo.getEnProceso()
    fun getAceptadasYEnProceso(): Flow<List<Cita>> = repo.getAceptadasYEnProceso()
    fun getEnTaller(): Flow<List<Cita>> = repo.getEnTaller()
    fun getFinalizados(): Flow<List<Cita>> = repo.getFinalizados()
    fun buscarPorPlaca(placa: String): Flow<List<Cita>> = repo.buscarFinalizadosPorPlaca(placa)
    fun getPorFecha(inicio: Long, fin: Long): Flow<List<Cita>> = repo.getFinalizadosPorFecha(inicio, fin)
    fun getCitasPorCliente(uid: String): Flow<List<Cita>> = repo.getCitasPorCliente(uid)
    fun getCitasPorEmail(email: String): Flow<List<Cita>> = repo.getCitasPorEmail(email)
}
