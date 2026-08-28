package com.taller.motostock.feature.inventory.viewmodel

import com.taller.motostock.core.domain.usecase.inventory.DeleteRepuestoUseCase
import com.taller.motostock.core.domain.usecase.inventory.GetRepuestosUseCase
import com.taller.motostock.core.domain.usecase.inventory.SaveRepuestoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class InventarioViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val getRepuestos: GetRepuestosUseCase = mock()
    private val saveRepuesto: SaveRepuestoUseCase = mock()
    private val deleteRepuesto: DeleteRepuestoUseCase = mock()

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        whenever(getRepuestos()).thenReturn(flowOf(emptyList()))
        whenever(getRepuestos.stockBajo()).thenReturn(flowOf(emptyList()))
    }

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `estado inicial carga listas vacias`() = runTest {
        val viewModel = InventarioViewModel(getRepuestos, saveRepuesto, deleteRepuesto)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.repuestos.isEmpty())
        assertTrue(viewModel.uiState.value.stockBajo.isEmpty())
    }
}
