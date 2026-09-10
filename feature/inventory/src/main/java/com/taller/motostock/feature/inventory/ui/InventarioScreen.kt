package com.taller.motostock.feature.inventory.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
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
        (it.nombre.contains(query, ignoreCase = true) || it.categoria.contains(query, ignoreCase = true) || it.proveedor.contains(query, ignoreCase = true))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            if (effect is InventarioContract.Effect.ShowMessage) {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoStockDs.colors.surface)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    text = "Inventario de Repuestos",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.onSurface,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            IconButton(
                onClick = { navController.navigate("inventario/form") },
                modifier = Modifier
                    .size(40.dp)
                    .background(MotoStockDs.colors.primary, shape = MotoStockDs.shapes.full)
                    .shadow(2.dp, shape = MotoStockDs.shapes.full)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo Repuesto",
                    tint = MotoStockDs.colors.onPrimary
                )
            }
        }

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MotoStockDs.colors.onSurfaceVariant
                )
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                    cursorBrush = SolidColor(MotoStockDs.colors.primary),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                "Buscar por nombre, código o categoría...",
                                style = MotoStockDs.typography.bodyMedium,
                                color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            item {
                CategoryChip(
                    label = CategoriasRepuesto.TODAS,
                    isSelected = selectedCategory == CategoriasRepuesto.TODAS,
                    onClick = { selectedCategory = CategoriasRepuesto.TODAS }
                )
            }
            items(CategoriasRepuesto.LISTA) { category ->
                CategoryChip(
                    label = category,
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MotoStockDs.colors.primary)
            }
        } else if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No se encontraron repuestos",
                    style = MotoStockDs.typography.bodyMedium,
                    color = MotoStockDs.colors.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList) { repuesto ->
                    RepuestoItemCard(
                        repuesto = repuesto,
                        onEdit = { navController.navigate("inventario/form?repuestoId=${repuesto.id}") },
                        onDelete = { viewModel.onIntent(InventarioContract.Intent.Eliminar(repuesto.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) MotoStockDs.colors.primary else MotoStockDs.colors.surfaceContainer,
                shape = MotoStockDs.shapes.full
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MotoStockDs.typography.labelSmall,
            color = if (isSelected) MotoStockDs.colors.onPrimary else MotoStockDs.colors.onSurfaceVariant
        )
    }
}

@Composable
fun RepuestoItemCard(
    repuesto: Repuesto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = MotoStockDs.shapes.medium)
            .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono e información
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (repuesto.stockBajo) MotoStockDs.colors.warningContainer else MotoStockDs.colors.primaryContainer,
                            shape = MotoStockDs.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (repuesto.stockBajo) MotoStockDs.colors.warning else MotoStockDs.colors.primary
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = repuesto.nombre,
                        style = MotoStockDs.typography.labelLarge,
                        color = MotoStockDs.colors.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${repuesto.categoria} • Stock:",
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.onSurfaceVariant
                        )
                        val stockColor = if (repuesto.stockBajo) MotoStockDs.colors.warning else MotoStockDs.colors.success
                        val stockSuffix = if (repuesto.stockBajo) " (Bajo)" else ""
                        Text(
                            text = "${repuesto.cantidad} unid.$stockSuffix",
                            style = MotoStockDs.typography.labelSmall,
                            color = stockColor
                        )
                    }
                }
            }

            // Precio, proveedor y acciones
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "S/. ${String.format("%.2f", repuesto.precioVenta)}",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.onSurface
                )
                if (repuesto.proveedor.isNotBlank()) {
                    Text(
                        text = repuesto.proveedor,
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MotoStockDs.colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MotoStockDs.colors.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
