package com.taller.motostock.feature.inventory.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.domain.model.CategoriasRepuesto
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.feature.inventory.InventarioContract
import com.taller.motostock.feature.inventory.viewmodel.InventarioViewModel

@Composable
fun InventarioScreen(
    navController: NavController,
    viewModel: InventarioViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CategoriasRepuesto.TODAS) }

    val filteredList = state.repuestos.filter { 
        (selectedCategory == CategoriasRepuesto.TODAS || it.categoria.equals(selectedCategory, ignoreCase = true)) &&
        (it.nombre.contains(query, ignoreCase = true) || it.categoria.contains(query, ignoreCase = true))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            if (effect is InventarioContract.Effect.ShowMessage) {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("inventario/form") },
                containerColor = MotoStockDs.colors.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Repuesto", tint = MotoStockDs.colors.onSecondary)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = MotoStockDs.spacing.medium)
        ) {
            Spacer(modifier = Modifier.height(MotoStockDs.spacing.medium))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar repuesto...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Text("X") } }
            )
            
            Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))
            
            LazyRow(horizontalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                item {
                    FilterChip(
                        selected = selectedCategory == CategoriasRepuesto.TODAS,
                        onClick = { selectedCategory = CategoriasRepuesto.TODAS },
                        label = { Text(CategoriasRepuesto.TODAS) }
                    )
                }
                items(CategoriasRepuesto.LISTA) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(MotoStockDs.spacing.medium))
            } else if (filteredList.isEmpty()) {
                Text(
                    text = "Sin repuestos registrados",
                    modifier = Modifier.padding(MotoStockDs.spacing.medium),
                    style = MotoStockDs.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList) { repuesto ->
                        RepuestoItem(
                            repuesto = repuesto,
                            onEdit = { navController.navigate("inventario/form?repuestoId=${repuesto.id}") },
                            onDelete = { viewModel.onIntent(InventarioContract.Intent.Eliminar(repuesto.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RepuestoItem(
    repuesto: Repuesto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(MotoStockDs.elevation.small),
        shape = MotoStockDs.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(MotoStockDs.spacing.small)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repuesto.nombre,
                    style = MotoStockDs.typography.h3,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MotoStockDs.colors.onSurface
                )
                Text(
                    text = repuesto.categoria,
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.7f),
                    style = MotoStockDs.typography.bodySmall
                )
                Text(
                    text = "Stock: ${repuesto.cantidad}",
                    color = if (repuesto.stockBajo) MotoStockDs.colors.error else MotoStockDs.colors.onSurface,
                    fontWeight = if (repuesto.stockBajo) androidx.compose.ui.text.font.FontWeight.Bold else null,
                    style = MotoStockDs.typography.bodyMedium
                )
                Text(
                    text = "S/. ${String.format("%.2f", repuesto.precioVenta)}",
                    color = MotoStockDs.colors.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    style = MotoStockDs.typography.bodyLarge
                )
                Text(
                    text = "Proveedor: ${repuesto.proveedor}",
                    style = MotoStockDs.typography.bodySmall,
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.7f)
                )
                
                if (repuesto.stockBajo) {
                    Text(
                        text = "⚠️ Stock bajo",
                        color = MotoStockDs.colors.error,
                        style = MotoStockDs.typography.labelSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
            Column {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MotoStockDs.colors.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MotoStockDs.colors.error
                    )
                }
            }
        }
    }
}
