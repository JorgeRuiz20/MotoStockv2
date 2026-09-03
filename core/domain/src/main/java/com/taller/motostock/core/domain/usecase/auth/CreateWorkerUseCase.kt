package com.taller.motostock.core.domain.usecase.auth

import com.taller.motostock.core.domain.repository.AuthRepository
import javax.inject.Inject

class CreateWorkerUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<Unit> =
        authRepository.crearTrabajador(email, password, name)
}
