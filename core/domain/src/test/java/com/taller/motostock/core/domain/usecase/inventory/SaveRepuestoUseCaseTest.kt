package com.taller.motostock.core.domain.usecase.inventory

import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.core.domain.repository.RepuestoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.never
import org.mockito.kotlin.any

/**
 * Pruebas unitarias para SaveRepuestoUseCase.
 * Verifica las reglas de negocio antes de persistir un repuesto.
 */
class SaveRepuestoUseCaseTest {

    // SUT (System Under Test)
    private lateinit var saveRepuestoUseCase: SaveRepuestoUseCase

    // Dependencia mockeada
    private val repository: RepuestoRepository = mock()

    @Before
    fun setUp() {
        saveRepuestoUseCase = SaveRepuestoUseCase(repository)
    }

    // ─── Test 1: nombre vacío lanza excepción ────────────────────────────────

    @Test
    fun `dado repuesto con nombre vacio, cuando se invoca, entonces lanza IllegalArgumentException`() = runTest {
        // Arrange
        val repuesto = Repuesto(
            id = "",
            nombre = "",          // nombre inválido
            cantidad = 10,
            precioVenta = 50.0
        )

        // Act
        val resultado = runCatching { saveRepuestoUseCase(repuesto) }

        // Assert
        assert(resultado.isFailure)
        assertEquals("El nombre no puede estar vacío", resultado.exceptionOrNull()?.message)
    }

    // ─── Test 2: cantidad negativa lanza excepción ───────────────────────────

    @Test
    fun `dado repuesto con cantidad negativa, cuando se invoca, entonces lanza IllegalArgumentException`() = runTest {
        // Arrange
        val repuesto = Repuesto(
            id = "",
            nombre = "Filtro de aceite",
            cantidad = -1,        // cantidad inválida
            precioVenta = 25.0
        )

        // Act
        val resultado = runCatching { saveRepuestoUseCase(repuesto) }

        // Assert
        assert(resultado.isFailure)
        assertEquals("La cantidad no puede ser negativa", resultado.exceptionOrNull()?.message)
    }

    // ─── Test 3: repuesto nuevo válido llama a repository.save ──────────────

    @Test
    fun `dado repuesto nuevo valido, cuando se invoca, entonces llama a repository save`() = runTest {
        // Arrange
        val repuesto = Repuesto(
            id = "",              // id vacío = nuevo repuesto
            nombre = "Pastilla de freno",
            cantidad = 20,
            precioVenta = 80.0
        )

        // Act
        saveRepuestoUseCase(repuesto)

        // Assert
        verify(repository).save(repuesto)
        verify(repository, never()).update(any())
    }
}
