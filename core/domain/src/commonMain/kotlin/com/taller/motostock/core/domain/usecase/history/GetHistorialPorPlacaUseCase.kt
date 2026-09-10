package com.taller.motostock.core.domain.usecase.history

import com.taller.motostock.core.domain.repository.HistorialRepository
import com.taller.motostock.core.domain.di.Inject

class GetHistorialPorPlacaUseCase @Inject constructor(private val repository: HistorialRepository) {
    operator fun invoke(placa: String) =
        if (placa.isBlank()) repository.getAll() else repository.getByPlaca(placa)
}
