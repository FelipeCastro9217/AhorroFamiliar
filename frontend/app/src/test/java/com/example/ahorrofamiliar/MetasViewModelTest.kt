package com.ahorro.familiar

import com.ahorro.familiar.ui.viewmodel.MetasViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class MetasViewModelTest {

    private lateinit var viewModel: MetasViewModel

    @Before
    fun setUp() {
        viewModel = MetasViewModel()
    }

    @Test
    fun `calcularPorcentaje con valores normales retorna 25`() {
        val resultado = viewModel.calcularPorcentaje(500000.0, 2000000.0)
        assertEquals(25, resultado)
    }

    @Test
    fun `calcularPorcentaje sin recaudar nada retorna 0`() {
        val resultado = viewModel.calcularPorcentaje(0.0, 2000000.0)
        assertEquals(0, resultado)
    }

    @Test
    fun `calcularPorcentaje cuando se alcanzo la meta retorna 100`() {
        val resultado = viewModel.calcularPorcentaje(2000000.0, 2000000.0)
        assertEquals(100, resultado)
    }

    @Test
    fun `calcularPorcentaje no supera 100 aunque se pague de mas`() {
        val resultado = viewModel.calcularPorcentaje(3000000.0, 2000000.0)
        assertEquals(100, resultado)
    }

    @Test
    fun `calcularPorcentaje con valorTotal cero retorna 0 sin dividir por cero`() {
        val resultado = viewModel.calcularPorcentaje(100.0, 0.0)
        assertEquals(0, resultado)
    }

    @Test
    fun `calcularPorcentaje con meta al 80 por ciento`() {
        val resultado = viewModel.calcularPorcentaje(800000.0, 1000000.0)
        assertEquals(80, resultado)
    }
}
