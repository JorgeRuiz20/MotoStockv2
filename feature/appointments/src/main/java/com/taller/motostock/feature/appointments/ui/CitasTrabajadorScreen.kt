package com.taller.motostock.feature.appointments.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.EstadoCita
import com.taller.motostock.feature.appointments.CitasContract
import com.taller.motostock.feature.appointments.viewmodel.CitasViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CitasTrabajadorScreen(
    navController: NavController,
    viewModel: CitasViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pendientes", "Aceptadas / En proceso")

    var showRejectDialog by remember { mutableStateOf<Cita?>(null) }
    var showCancelDialog by remember { mutableStateOf<Cita?>(null) }
    var showFinalizarDialog by remember { mutableStateOf<Cita?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            if (effect is CitasContract.Effect.ShowMessage) {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MotoStockDs.colors.surface,
            contentColor = MotoStockDs.colors.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MotoStockDs.typography.labelLarge) }
                )
            }
        }

        val currentList = if (selectedTab == 0) state.pendientes else state.activas

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = if (selectedTab == 0) "No hay citas pendientes" else "No hay citas aceptadas o en proceso",
                    style = MotoStockDs.typography.bodyMedium,
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MotoStockDs.spacing.small),
                verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)
            ) {
                items(currentList) { cita ->
                    CitaItem(
                        cita = cita,
                        onAceptar = { viewModel.onIntent(CitasContract.Intent.Aceptar(cita)) },
                        onRechazar = { showRejectDialog = cita },
                        onPonerEnProceso = { viewModel.onIntent(CitasContract.Intent.PonerEnProceso(cita)) },
                        onFinalizar = { showFinalizarDialog = cita },
                        onCancelar = { showCancelDialog = cita }
                    )
                }
            }
        }
    }

    if (showRejectDialog != null) {
        MotivoDialog(
            title = "Rechazar cita",
            message = "Placa: ${showRejectDialog!!.placa}\n${showRejectDialog!!.propietario}",
            onDismiss = { showRejectDialog = null },
            onConfirm = { motivo ->
                viewModel.onIntent(CitasContract.Intent.Rechazar(showRejectDialog!!, motivo))
                showRejectDialog = null
            }
        )
    }

    if (showCancelDialog != null) {
        MotivoDialog(
            title = "Cancelar cita",
            message = "Placa: ${showCancelDialog!!.placa}\n${showCancelDialog!!.propietario}",
            onDismiss = { showCancelDialog = null },
            onConfirm = { motivo ->
                viewModel.onIntent(CitasContract.Intent.Cancelar(showCancelDialog!!, motivo))
                showCancelDialog = null
            }
        )
    }

    if (showFinalizarDialog != null) {
        FinalizarServicioDialog(
            cita = showFinalizarDialog!!,
            repuestosDisponibles = state.repuestos,
            onDismiss = { showFinalizarDialog = null },
            onConfirm = { productos, costo ->
                viewModel.onIntent(CitasContract.Intent.Finalizar(showFinalizarDialog!!, productos, costo))
                showFinalizarDialog = null
            }
        )
    }
}

@Composable
fun CitaItem(
    cita: Cita,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    onPonerEnProceso: () -> Unit,
    onFinalizar: () -> Unit,
    onCancelar: () -> Unit
) {
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
                    text = if (cita.estado == EstadoCita.PENDIENTE) "⏳ Pendiente" else if (cita.estado == EstadoCita.EN_PROCESO) "🛠️ En proceso" else "✅ Aceptada",
                    style = MotoStockDs.typography.labelMedium,
                    color = if (cita.estado == EstadoCita.PENDIENTE) Color(0xFFF57C00) else if (cita.estado == EstadoCita.EN_PROCESO) MotoStockDs.colors.secondary else MotoStockDs.colors.success
                )
            }
            Text(
                text = cita.propietario.ifEmpty { "Sin nombre" },
                style = MotoStockDs.typography.bodyLarge,
                color = MotoStockDs.colors.onSurface
            )
            Text(
                text = "Modelo: ${cita.modelo.ifEmpty { "No especificado" }}",
                style = MotoStockDs.typography.bodySmall,
                color = MotoStockDs.colors.onSurface.copy(alpha = 0.7f)
            )
            if (cita.telefono.isNotBlank()) {
                Text(
                    text = "Tel: ${cita.telefono}",
                    style = MotoStockDs.typography.bodySmall,
                    color = MotoStockDs.colors.onSurface
                )
            }
            Text(
                text = cita.tipoServicio,
                style = MotoStockDs.typography.bodyMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MotoStockDs.colors.primary
            )
            if (cita.descripcion.isNotBlank()) {
                Text(
                    text = cita.descripcion,
                    style = MotoStockDs.typography.bodySmall,
                    color = MotoStockDs.colors.onSurface
                )
            }
            Text(
                text = "Ingreso: ${fmt.format(Date(cita.fechaIngreso))} ${cita.horaIngreso}",
                style = MotoStockDs.typography.labelSmall,
                color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f)
            )
            
            if (cita.horaDeseada.isNotBlank()) {
                Text(
                    text = "⏰ Hora deseada: ${cita.horaDeseada}",
                    style = MotoStockDs.typography.labelSmall,
                    color = MotoStockDs.colors.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))
            
            if (cita.estado == EstadoCita.PENDIENTE) {
                Row(horizontalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                    Button(
                        onClick = onAceptar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.success)
                    ) {
                        Text("Aceptar")
                    }
                    Button(
                        onClick = onRechazar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.error)
                    ) {
                        Text("Rechazar")
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                        if (cita.estado != EstadoCita.EN_PROCESO) {
                            Button(
                                onClick = onPonerEnProceso,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.secondary)
                            ) {
                                Text("En proceso")
                            }
                        }
                        Button(
                            onClick = onFinalizar,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.success)
                        ) {
                            Text("Finalizar")
                        }
                    }
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MotoStockDs.colors.error)
                    ) {
                        Text("Cancelar cita")
                    }
                }
            }
        }
    }
}

@Composable
fun MotivoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MotoStockDs.typography.h3) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                Text(message, style = MotoStockDs.typography.bodyMedium)
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(motivo) },
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

@Composable
private fun FinalizarServicioDialog(
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
                Text("Selecciona los productos utilizados:", style = MotoStockDs.typography.bodyMedium)
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(repuestosDisponibles) { repuesto ->
                        val cantidad = seleccionados[repuesto.id] ?: 0
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Checkbox(
                                checked = cantidad > 0,
                                onCheckedChange = { checked ->
                                    if (checked) seleccionados[repuesto.id] = 1 else seleccionados.remove(repuesto.id)
                                }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(repuesto.nombre, style = MotoStockDs.typography.bodyMedium)
                                Text("Stock: ${repuesto.cantidad}", style = MotoStockDs.typography.bodySmall)
                            }
                            if (cantidad > 0) {
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    IconButton(onClick = { if (cantidad > 1) seleccionados[repuesto.id] = cantidad - 1 }) { Text("-") }
                                    Text(cantidad.toString())
                                    IconButton(onClick = { if (cantidad < repuesto.cantidad) seleccionados[repuesto.id] = cantidad + 1 }) { Text("+") }
                                }
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = costo,
                    onValueChange = { costo = it },
                    label = { Text("Costo total (S/)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val items = repuestosDisponibles.filter { seleccionados.containsKey(it.id) }
                    .map { it to (seleccionados[it.id] ?: 1) }
                onConfirm(items, costo.toDoubleOrNull() ?: 0.0)
            }) { Text("Confirmar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
