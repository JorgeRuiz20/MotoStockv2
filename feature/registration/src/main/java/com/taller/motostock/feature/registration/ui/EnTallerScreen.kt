package com.taller.motostock.feature.registration.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.domain.model.Cita
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
            .padding(MotoStockDs.spacing.medium)
    ) {
        Text(
            text = "Vehículos en atención",
            style = MotoStockDs.typography.h3,
            color = MotoStockDs.colors.primary
        )
        Text(
            text = "Motos actualmente en el taller",
            style = MotoStockDs.typography.bodySmall,
            color = MotoStockDs.colors.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.medium))

        if (state.vehiculosEnTaller.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(
                    text = "No hay vehículos en atención actualmente",
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f),
                    style = MotoStockDs.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
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
    val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(MotoStockDs.elevation.small),
        shape = MotoStockDs.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(MotoStockDs.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.extraSmall)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = cita.placa,
                    style = MotoStockDs.typography.h2,
                    color = MotoStockDs.colors.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "🛠️ EN TALLER",
                    style = MotoStockDs.typography.labelMedium,
                    color = MotoStockDs.colors.secondary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            Text(
                text = cita.modelo.ifEmpty { "Modelo no especificado" },
                color = MotoStockDs.colors.onSurface.copy(alpha = 0.7f),
                style = MotoStockDs.typography.bodySmall
            )
            Text(
                text = cita.propietario.ifEmpty { "Sin nombre" },
                style = MotoStockDs.typography.bodyLarge,
                color = MotoStockDs.colors.onSurface
            )
            Text(
                text = "Ingresó: ${fmt.format(Date(cita.fechaIngreso))} ${cita.horaIngreso}",
                style = MotoStockDs.typography.bodySmall,
                color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = cita.tipoServicio.ifEmpty { "Sin tipo especificado" },
                color = MotoStockDs.colors.primary,
                style = MotoStockDs.typography.bodyMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            Button(
                onClick = onFinalizar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MotoStockDs.spacing.small),
                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.success)
            ) {
                Text("✓ Finalizar servicio")
            }
        }
    }
}

@Composable
fun FinalizarServicioDialog(
    cita: Cita,
    repuestosDisponibles: List<com.taller.motostock.core.domain.model.Repuesto>,
    onDismiss: () -> Unit,
    onConfirm: (List<Pair<com.taller.motostock.core.domain.model.Repuesto, Int>>, Double) -> Unit
) {
    var costo by remember { mutableStateOf("") }
    val seleccionados = remember { mutableStateMapOf<String, Int>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Finalizar servicio - ${cita.placa}", style = MotoStockDs.typography.h3) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                Text(
                    text = "Selecciona los productos utilizados:",
                    style = MotoStockDs.typography.bodyMedium
                )
                
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(repuestosDisponibles) { repuesto ->
                        val cantidad = seleccionados[repuesto.id] ?: 0
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Checkbox(
                                checked = cantidad > 0,
                                onCheckedChange = { checked ->
                                    if (checked) seleccionados[repuesto.id] = 1 else seleccionados.remove(repuesto.id)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = MotoStockDs.colors.primary)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = repuesto.nombre,
                                    style = MotoStockDs.typography.bodyMedium,
                                    color = MotoStockDs.colors.onSurface
                                )
                                Text(
                                    text = "Stock: ${repuesto.cantidad} | S/ ${repuesto.precioVenta}",
                                    style = MotoStockDs.typography.bodySmall,
                                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            if (cantidad > 0) {
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    IconButton(onClick = { if (cantidad > 1) seleccionados[repuesto.id] = cantidad - 1 }) {
                                        Text("-", color = MotoStockDs.colors.primary)
                                    }
                                    Text(text = cantidad.toString(), style = MotoStockDs.typography.bodyMedium)
                                    IconButton(onClick = { if (cantidad < repuesto.cantidad) seleccionados[repuesto.id] = cantidad + 1 }) {
                                        Text("+", color = MotoStockDs.colors.primary)
                                    }
                                }
                            }
                        }
                    }
                }
                
                OutlinedTextField(
                    value = costo,
                    onValueChange = { costo = it },
                    label = { Text("Costo total (S/)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
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
                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { 
                Text("Cancelar", color = MotoStockDs.colors.secondary) 
            }
        }
    )
}
