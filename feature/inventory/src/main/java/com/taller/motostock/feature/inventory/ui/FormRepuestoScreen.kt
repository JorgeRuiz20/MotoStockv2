package com.taller.motostock.feature.inventory.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.domain.model.CategoriasRepuesto
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.feature.inventory.InventarioContract
import com.taller.motostock.feature.inventory.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormRepuestoScreen(
    navController: NavController,
    repuestoId: String? = null,
    viewModel: InventarioViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val repuestoEditable = remember(state.repuestos, repuestoId) {
        state.repuestos.find { it.id == repuestoId }
    }

    var nombre by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.nombre ?: "") }
    var categoria by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.categoria ?: CategoriasRepuesto.LISTA.firstOrNull() ?: "") }
    var cantidad by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.cantidad?.toString() ?: "10") }
    var precioCompra by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.precioCompra?.toString() ?: "0.0") }
    var precioVenta by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.precioVenta?.toString() ?: "15.0") }
    var stockMinimo by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.stockMinimo?.toString() ?: "5") }
    var proveedor by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.proveedor ?: "") }

    var expandedCategory by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is InventarioContract.Effect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                InventarioContract.Effect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoStockDs.colors.surface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(40.dp)
                    .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.full)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = MotoStockDs.colors.primary
                )
            }
            Text(
                text = if (repuestoId == null) "Agregar Nuevo Repuesto" else "Editar Repuesto",
                style = MotoStockDs.typography.h3,
                color = MotoStockDs.colors.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Form Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, shape = MotoStockDs.shapes.medium)
                .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Nombre
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nombre del Repuesto *", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (nombre.isEmpty()) Text("Ej. Pastillas de Freno Brembo", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Categoría Dropdown
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Categoría", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = if (categoria.isEmpty()) "Seleccionar categoría" else categoria,
                                style = MotoStockDs.typography.bodyMedium,
                                color = MotoStockDs.colors.onSurface
                            )
                        }
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false },
                            modifier = Modifier.background(MotoStockDs.colors.surfaceContainerLowest)
                        ) {
                            CategoriasRepuesto.LISTA.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item, style = MotoStockDs.typography.bodyMedium) },
                                    onClick = {
                                        categoria = item
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Grid 2 columnas: Stock Inicial & Precio Venta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Stock Inicial", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                        BasicTextField(
                            value = cantidad,
                            onValueChange = { cantidad = it },
                            textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush = SolidColor(MotoStockDs.colors.primary),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Precio Venta (S/.)", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                        BasicTextField(
                            value = precioVenta,
                            onValueChange = { precioVenta = it },
                            textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            cursorBrush = SolidColor(MotoStockDs.colors.primary),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                // Grid 2 columnas: Precio Compra & Stock Mínimo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Precio Compra (S/.)", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                        BasicTextField(
                            value = precioCompra,
                            onValueChange = { precioCompra = it },
                            textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            cursorBrush = SolidColor(MotoStockDs.colors.primary),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Stock Mínimo", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                        BasicTextField(
                            value = stockMinimo,
                            onValueChange = { stockMinimo = it },
                            textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush = SolidColor(MotoStockDs.colors.primary),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                // Proveedor
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Proveedor", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = proveedor,
                        onValueChange = { proveedor = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (proveedor.isEmpty()) Text("Nombre del proveedor o distribuidor", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Guardar
                Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            Toast.makeText(context, "El nombre es requerido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val repuesto = Repuesto(
                            id = repuestoId ?: "",
                            nombre = nombre,
                            categoria = categoria,
                            cantidad = cantidad.toIntOrNull() ?: 0,
                            precioCompra = precioCompra.toDoubleOrNull() ?: 0.0,
                            precioVenta = precioVenta.toDoubleOrNull() ?: 0.0,
                            stockMinimo = stockMinimo.toIntOrNull() ?: 5,
                            proveedor = proveedor
                        )
                        viewModel.onIntent(InventarioContract.Intent.Guardar(repuesto))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving,
                    shape = MotoStockDs.shapes.medium,
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.secondary,
                        contentColor = MotoStockDs.colors.onSecondary
                    )
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MotoStockDs.colors.onSecondary
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text("Guardar Repuesto", style = MotoStockDs.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
