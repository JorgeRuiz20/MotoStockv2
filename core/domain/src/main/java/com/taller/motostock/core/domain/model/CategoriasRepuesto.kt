package com.taller.motostock.core.domain.model

/** Categorías predefinidas de repuestos de motocicleta, usadas en el formulario
 * (dropdown) y en el filtro de Inventario. Basadas en la clasificación estándar
 * que usan tiendas y talleres de repuestos de moto. */
object CategoriasRepuesto {
    const val TODAS = "Todas"

    val LISTA = listOf(
        "Motor",
        "Transmisión",
        "Frenos",
        "Suspensión y Chasis",
        "Sistema Eléctrico",
        "Ruedas y Neumáticos",
        "Carrocería y Plásticos",
        "Escape",
        "Refrigeración",
        "Aceites y Lubricantes",
        "Filtros",
        "Accesorios",
        "Otros"
    )
}
