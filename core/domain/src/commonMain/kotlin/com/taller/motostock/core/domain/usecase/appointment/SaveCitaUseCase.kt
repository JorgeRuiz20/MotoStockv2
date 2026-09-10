package com.taller.motostock.core.domain.usecase.appointment

import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.repository.CitaRepository
import com.taller.motostock.core.domain.di.Inject

class SaveCitaUseCase @Inject constructor(private val repository: CitaRepository) {
    suspend operator fun invoke(cita: Cita) = repository.save(cita)
}
