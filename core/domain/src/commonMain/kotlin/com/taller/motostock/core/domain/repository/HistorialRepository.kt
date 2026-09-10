package com.taller.motostock.core.domain.repository

import com.taller.motostock.core.domain.model.Moto
import com.taller.motostock.core.domain.model.ServicioHistorial
import kotlinx.coroutines.flow.Flow

interface HistorialRepository {
    fun getByPlaca(placa: String): Flow<List<ServicioHistorial>>
    fun getAll(): Flow<List<ServicioHistorial>>
    suspend fun save(servicio: ServicioHistorial)
    suspend fun update(servicio: ServicioHistorial)
    suspend fun getMoto(placa: String): Moto?
    suspend fun saveMoto(moto: Moto)
    suspend fun buscarPlacaEnApi(placa: String): Result<Moto>
}
