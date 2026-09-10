package com.taller.motostock.core.domain.usecase.history

import com.taller.motostock.core.domain.model.ServicioHistorial
import com.taller.motostock.core.domain.repository.HistorialRepository
import com.taller.motostock.core.domain.di.Inject

class SaveServicioUseCase @Inject constructor(
    private val repository: HistorialRepository
) {
    suspend operator fun invoke(servicio: ServicioHistorial) {
        require(servicio.placa.isNotBlank()) { "La placa es requerida" }
        require(servicio.descripcionProblema.isNotBlank()) { "Describa el problema" }
        if (servicio.id.isEmpty()) repository.save(servicio)
        else repository.update(servicio)
    }
}
