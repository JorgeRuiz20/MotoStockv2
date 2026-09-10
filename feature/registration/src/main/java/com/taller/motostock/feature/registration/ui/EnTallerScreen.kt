package com.taller.motostock.feature.registration.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.Repuesto
import com.taller.motostock.feature.registration.RegistroVehicularContract
import com.taller.motostock.feature.registration.viewmodel.RegistroVehicularViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EnTallerScreen(
    navController: NavController,
    viewModel: RegistroVehicularViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showFinalizarDialog by remember { mutableStateOf<Cita?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            if (effect is RegistroVehicularContract.Effect.ShowMessage) {
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
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "Vehículos en Atención",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.onSurface
                )
                Text(
                    text = "Motos actualmente en el taller",
                    style = MotoStockDs.typography.bodySmall,
                    color = MotoStockDs.colors.onSurfaceVariant
                )
            }
        }

        if (state.vehiculosEnTaller.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No hay vehículos en atención actualmente",
                    color = MotoStockDs.colors.onSurfaceVariant,
                    style = MotoStockDs.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.vehiculosEnTaller) { cita ->
                    EnTallerItem(cita = cita, onFinalizar = { showFinalizarDialog = cita })
                }
            }
        }
    }

    if (showFinalizarDialog != null) {
        FinalizarServicioDialog(
            cita = showFinalizarDialog!!,
            repuestosDisponibles = state.repuestos,
            onDismiss = { showFinalizarDialog = null },
            onConfirm = { productos, costo ->
                viewModel.onIntent(RegistroVehicularContract.Intent.Finalizar(showFinalizarDialog!!, productos, costo))
                showFinalizarDialog = null
            }
        )
    }
}

@Composable
fun EnTallerItem(cita: Cita, onFinalizar: () -> Unit) {
    val fmt = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = MotoStockDs.shapes.medium)
            .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Acento lateral
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .defaultMinSize(minHeight = 120.dp)
                    .background(MotoStockDs.colors.primary)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header: Badge y fecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(MotoStockDs.colors.infoContainer, shape = MotoStockDs.shapes.full)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MotoStockDs.colors.primary
                            )
                            Text(
                                text = "En Proceso",
                                style = MotoStockDs.typography.labelSmall,
                                color = MotoStockDs.colors.primary
                            )
                        }
                    }

                    Text(
                        text = fmt.format(Date(cita.fechaIngreso)),
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                }

                // Datos de la moto
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "${cita.modelo.ifEmpty { "Motocicleta" }} (${cita.placa})",
                        style = MotoStockDs.typography.h3,
                        color = MotoStockDs.colors.primary
                    )
                    Text(
                        text = "Cliente: ${cita.propietario.ifEmpty { "Sin nombre" }} • ${cita.tipoServicio.ifEmpty { "Mantenimiento" }}",
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                    if (cita.telefono.isNotBlank()) {
                        Text(
                            text = "Tel: ${cita.telefono}",
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.onSurfaceVariant
                        )
                    }
                }

                // Botón finalizar
                Button(
                    onClick = onFinalizar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.small,
                    contentPadding = PaddingValues(vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.surfaceContainerHighest,
                        contentColor = MotoStockDs.colors.onSurface
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("Finalizar Servicio", style = MotoStockDs.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun FinalizarServicioDialog(
    cita: Cita,
    repuestosDisponibles: List<Repuesto>,
    onDismiss: () -> Unit,
    onConfirm: (List<Pair<Repuesto, Int>>, Double) -> Unit
) {
    var costo by remember { mutableStateOf("") }
    val seleccionados = remember { mutableStateMapOf<String, Int>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MotoStockDs.colors.surfaceContainerLowest,
        titleContentColor = MotoStockDs.colors.primary,
        title = { Text("Finalizar servicio - ${cita.placa}", style = MotoStockDs.typography.h3) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Selecciona los productos utilizados:",
                    style = MotoStockDs.typography.bodyMedium,
                    color = MotoStockDs.colors.onSurfaceVariant
                )

                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(repuestosDisponibles) { repuesto ->
                        val cantidad = seleccionados[repuesto.id] ?: 0
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = cantidad > 0,
                                onCheckedChange = { checked ->
                                    if (checked) seleccionados[repuesto.id] = 1 else seleccionados.remove(repuesto.id)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MotoStockDs.colors.primary,
                                    checkmarkColor = MotoStockDs.colors.onPrimary
                                )
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = repuesto.nombre,
                                    style = MotoStockDs.typography.labelMedium,
                                    color = MotoStockDs.colors.onSurface
                                )
                                Text(
                                    text = "Stock: ${repuesto.cantidad} | S/. ${String.format("%.2f", repuesto.precioVenta)}",
                                    style = MotoStockDs.typography.bodySmall,
                                    color = MotoStockDs.colors.onSurfaceVariant
                                )
                            }
                            if (cantidad > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (cantidad > 1) seleccionados[repuesto.id] = cantidad - 1 },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("-", style = MotoStockDs.typography.labelLarge)
                                    }
                                    Text(
                                        text = cantidad.toString(),
                                        style = MotoStockDs.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    IconButton(
                                        onClick = { if (cantidad < repuesto.cantidad) seleccionados[repuesto.id] = cantidad + 1 },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("+", style = MotoStockDs.typography.labelLarge)
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = costo,
                    onValueChange = { costo = it },
                    label = { Text("Costo total (S/.)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.small,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lista = repuestosDisponibles.filter { seleccionados.containsKey(it.id) }
                        .map { it to (seleccionados[it.id] ?: 1) }
                    onConfirm(lista, costo.toDoubleOrNull() ?: 0.0)
                },
                shape = MotoStockDs.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MotoStockDs.colors.primary,
                    contentColor = MotoStockDs.colors.onPrimary
                )
            ) {
                Text("Confirmar", style = MotoStockDs.typography.labelMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MotoStockDs.colors.onSurfaceVariant, style = MotoStockDs.typography.labelMedium)
            }
        }
    )
}
