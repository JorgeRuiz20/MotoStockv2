package com.taller.motostock.core.domain.usecase.inventory

import com.taller.motostock.core.domain.repository.RepuestoRepository
import com.taller.motostock.core.domain.di.Inject

class DeleteRepuestoUseCase @Inject constructor(
    private val repository: RepuestoRepository
) {
    suspend operator fun invoke(id: String) = repository.delete(id)
}
