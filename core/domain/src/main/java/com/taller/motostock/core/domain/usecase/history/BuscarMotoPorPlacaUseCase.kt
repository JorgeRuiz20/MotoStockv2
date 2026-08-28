package com.taller.motostock.core.domain.usecase.history

import com.taller.motostock.core.domain.model.Moto
import com.taller.motostock.core.domain.repository.HistorialRepository
import javax.inject.Inject

class BuscarMotoPorPlacaUseCase @Inject constructor(
    private val repository: HistorialRepository
) {
    suspend operator fun invoke(placa: String): Result<Moto> {
        val placaLimpia = placa.uppercase().trim()
        // 1. Buscar en BD local primero
        val motoLocal = repository.getMoto(placaLimpia)
        if (motoLocal != null) return Result.success(motoLocal)
        // 2. Si no existe, consultar API externa
        return repository.buscarPlacaEnApi(placaLimpia)
    }
}
