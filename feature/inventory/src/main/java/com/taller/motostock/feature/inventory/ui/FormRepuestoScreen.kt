package com.taller.motostock.feature.inventory.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    var categoria by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.categoria ?: "") }
    var cantidad by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.cantidad?.toString() ?: "") }
    var precioCompra by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.precioCompra?.toString() ?: "") }
    var precioVenta by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.precioVenta?.toString() ?: "") }
    var stockMinimo by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.stockMinimo?.toString() ?: "5") }
    var proveedor by remember(repuestoEditable) { mutableStateOf(repuestoEditable?.proveedor ?: "") }

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is InventarioContract.Effect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                InventarioContract.Effect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (repuestoId == null) "Nuevo Repuesto" else "Editar Repuesto",
                        style = MotoStockDs.typography.h2
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Volver",
                            tint = MotoStockDs.colors.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MotoStockDs.colors.background,
                    titleContentColor = MotoStockDs.colors.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(MotoStockDs.spacing.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.medium)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth()
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CategoriasRepuesto.LISTA.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                categoria = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad en stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precioCompra,
                onValueChange = { precioCompra = it },
                label = { Text("Precio de compra (S/.)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precioVenta,
                onValueChange = { precioVenta = it },
                label = { Text("Precio de venta (S/.)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = stockMinimo,
                onValueChange = { stockMinimo = it },
                label = { Text("Stock mínimo (alerta)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = proveedor,
                onValueChange = { proveedor = it },
                label = { Text("Proveedor") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MotoStockDs.colors.primary)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            Toast.makeText(context, "Nombre es requerido", Toast.LENGTH_SHORT).show()
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
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(MotoStockDs.spacing.large),
                            color = MotoStockDs.colors.onPrimary
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
