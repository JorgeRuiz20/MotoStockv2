package com.taller.motostock.core.domain.repository

import com.taller.motostock.core.domain.model.Repuesto
import kotlinx.coroutines.flow.Flow

interface RepuestoRepository {
    fun getAll(): Flow<List<Repuesto>>
    fun getStockBajo(): Flow<List<Repuesto>>
    suspend fun getById(id: String): Repuesto?
    suspend fun save(repuesto: Repuesto)
    suspend fun update(repuesto: Repuesto)
    suspend fun delete(id: String)
}
