package com.taller.motostock.core.domain.usecase.inventory

import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.repository.RepuestoRepository
import kotlinx.coroutines.flow.Flow
import com.taller.motostock.core.domain.di.Inject

class GetRepuestosUseCase @Inject constructor(
    private val repository: RepuestoRepository
) {
    operator fun invoke(): Flow<List<Repuesto>> = repository.getAll()
    fun stockBajo(): Flow<List<Repuesto>> = repository.getStockBajo()
}
