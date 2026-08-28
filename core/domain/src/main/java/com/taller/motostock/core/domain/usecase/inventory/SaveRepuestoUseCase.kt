package com.taller.motostock.core.domain.usecase.inventory

import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.repository.RepuestoRepository
import javax.inject.Inject

class SaveRepuestoUseCase @Inject constructor(
    private val repository: RepuestoRepository
) {
    suspend operator fun invoke(repuesto: Repuesto) {
        require(repuesto.nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(repuesto.cantidad >= 0) { "La cantidad no puede ser negativa" }
        require(repuesto.precioVenta >= 0) { "El precio no puede ser negativo" }
        if (repuesto.id.isEmpty()) repository.save(repuesto)
        else repository.update(repuesto)
    }
}
