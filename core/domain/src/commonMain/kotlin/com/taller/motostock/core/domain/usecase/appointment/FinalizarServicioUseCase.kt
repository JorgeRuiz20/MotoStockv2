package com.taller.motostock.core.domain.usecase.appointment

import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.EstadoCita
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.repository.CitaRepository
import com.taller.motostock.core.domain.repository.RepuestoRepository
import com.taller.motostock.core.domain.util.DateTimeUtil
import com.taller.motostock.core.domain.di.Inject

class FinalizarServicioUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
    private val repuestoRepository: RepuestoRepository
) {
    suspend operator fun invoke(cita: Cita, productos: List<Pair<Repuesto, Int>>, costoServicio: Double): List<Repuesto> {
        require(cita.estado == EstadoCita.ACEPTADA || cita.estado == EstadoCita.EN_PROCESO) {
            "Solo se pueden finalizar servicios aceptados o en proceso"
        }
        require(costoServicio >= 0) { "El costo no puede ser negativo" }
        productos.forEach { (repuesto, cantidad) ->
            require(cantidad > 0) { "La cantidad utilizada debe ser mayor que cero" }
            require(cantidad <= repuesto.cantidad) { "Stock insuficiente para ${repuesto.nombre}" }
        }

        val now = DateTimeUtil.currentTimeMillis()
        citaRepository.update(
            cita.copy(
                estado = EstadoCita.FINALIZADO,
                fechaSalida = now,
                horaSalida = DateTimeUtil.formatTimeHHmm(now),
                repuestosUsadosJson = productos.toUsedPartsJson(),
                costoServicio = costoServicio
            )
        )

        return productos.map { (repuesto, cantidad) ->
            repuesto.copy(cantidad = repuesto.cantidad - cantidad).also { repuestoRepository.update(it) }
        }.filter { it.stockBajo }
    }

    private fun List<Pair<Repuesto, Int>>.toUsedPartsJson(): String = joinToString(prefix = "[", postfix = "]") { (part, quantity) ->
        "{\"id\":\"${part.id.jsonEscape()}\",\"nombre\":\"${part.nombre.jsonEscape()}\",\"cantidad\":$quantity,\"precioUnitario\":${part.precioVenta}}"
    }

    private fun String.jsonEscape(): String = replace("\\", "\\\\").replace("\"", "\\\"")
}
