package com.taller.motostock.feature.appointments.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.EstadoCita
import com.taller.motostock.core.domain.model.Repuesto
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
    val tabs = listOf("Todas", "Pendientes", "En Proceso")

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

    val currentList = when (selectedTab) {
        1 -> state.pendientes
        2 -> state.activas
        else -> state.pendientes + state.activas
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoStockDs.colors.surface)
            .padding(16.dp)
    ) {
        // Encabezado superior con botón volver y agregar
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
                    text = "Gestión de Citas",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.primary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            IconButton(
                onClick = { navController.navigate("agendar_cita") },
                modifier = Modifier
                    .size(40.dp)
                    .background(MotoStockDs.colors.primary, shape = MotoStockDs.shapes.full)
                    .shadow(2.dp, shape = MotoStockDs.shapes.full)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agendar Cita",
                    tint = MotoStockDs.colors.onPrimary
                )
            }
        }

        // Selector segmentado tipo pastilla (Filter segment)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MotoStockDs.colors.surfaceContainer, shape = MotoStockDs.shapes.medium)
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .shadow(1.dp, shape = MotoStockDs.shapes.small)
                                        .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.small)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MotoStockDs.typography.labelMedium,
                            color = if (isSelected) MotoStockDs.colors.primary else MotoStockDs.colors.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (selectedTab) {
                        1 -> "No hay citas pendientes"
                        2 -> "No hay citas en proceso"
                        else -> "No hay citas registradas"
                    },
                    style = MotoStockDs.typography.bodyMedium,
                    color = MotoStockDs.colors.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
            message = "Placa: ${showRejectDialog!!.placa}\nCliente: ${showRejectDialog!!.propietario}",
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
            message = "Placa: ${showCancelDialog!!.placa}\nCliente: ${showCancelDialog!!.propietario}",
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
    val fmt = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val borderAccentColor = when (cita.estado) {
        EstadoCita.PENDIENTE -> MotoStockDs.colors.warning
        EstadoCita.ACEPTADA -> MotoStockDs.colors.primary
        EstadoCita.EN_PROCESO -> MotoStockDs.colors.secondaryContainer
        EstadoCita.FINALIZADO -> MotoStockDs.colors.success
        EstadoCita.RECHAZADA, EstadoCita.CANCELADA -> MotoStockDs.colors.error
    }

    val statusBg = when (cita.estado) {
        EstadoCita.PENDIENTE -> MotoStockDs.colors.warningContainer
        EstadoCita.ACEPTADA, EstadoCita.EN_PROCESO -> MotoStockDs.colors.infoContainer
        EstadoCita.FINALIZADO -> MotoStockDs.colors.successContainer
        EstadoCita.RECHAZADA, EstadoCita.CANCELADA -> MotoStockDs.colors.errorContainer
    }

    val statusText = when (cita.estado) {
        EstadoCita.PENDIENTE -> MotoStockDs.colors.onWarningContainer
        EstadoCita.ACEPTADA, EstadoCita.EN_PROCESO -> MotoStockDs.colors.primary
        EstadoCita.FINALIZADO -> MotoStockDs.colors.success
        EstadoCita.RECHAZADA, EstadoCita.CANCELADA -> MotoStockDs.colors.onErrorContainer
    }

    val statusLabel = when (cita.estado) {
        EstadoCita.PENDIENTE -> "Pendiente"
        EstadoCita.ACEPTADA -> "Aceptada"
        EstadoCita.EN_PROCESO -> "En Proceso"
        EstadoCita.FINALIZADO -> "Finalizado"
        EstadoCita.RECHAZADA -> "Rechazada"
        EstadoCita.CANCELADA -> "Cancelada"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = MotoStockDs.shapes.medium)
            .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Acento lateral de color (border-l-4)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .defaultMinSize(minHeight = 120.dp)
                    .background(borderAccentColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header: Badge de estado y Hora
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(statusBg, shape = MotoStockDs.shapes.full)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            style = MotoStockDs.typography.labelSmall,
                            color = statusText
                        )
                    }

                    val fechaTexto = if (cita.horaDeseada.isNotBlank()) {
                        "Hora deseada: ${cita.horaDeseada}"
                    } else {
                        fmt.format(Date(cita.fechaIngreso))
                    }
                    Text(
                        text = fechaTexto,
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                }

                // Moto y Cliente
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    val motoTitle = if (cita.modelo.isNotBlank()) {
                        "${cita.modelo} (${cita.placa})"
                    } else {
                        "Placa: ${cita.placa}"
                    }
                    Text(
                        text = motoTitle,
                        style = MotoStockDs.typography.h3,
                        color = MotoStockDs.colors.primary
                    )
                    Text(
                        text = "Cliente: ${cita.propietario.ifEmpty { "Sin registrar" }} • ${cita.tipoServicio}",
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                    if (cita.descripcion.isNotBlank()) {
                        Text(
                            text = cita.descripcion,
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                    if (cita.motivoRechazo.isNotBlank()) {
                        Text(
                            text = "Motivo: ${cita.motivoRechazo}",
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.error
                        )
                    }
                }

                // Botones de acción según el estado
                when (cita.estado) {
                    EstadoCita.PENDIENTE -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onAceptar,
                                modifier = Modifier.weight(1f),
                                shape = MotoStockDs.shapes.small,
                                contentPadding = PaddingValues(vertical = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MotoStockDs.colors.secondary,
                                    contentColor = MotoStockDs.colors.onSecondary
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Aceptar", style = MotoStockDs.typography.labelMedium)
                                }
                            }

                            Button(
                                onClick = onRechazar,
                                modifier = Modifier.weight(1f),
                                shape = MotoStockDs.shapes.small,
                                contentPadding = PaddingValues(vertical = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MotoStockDs.colors.errorContainer,
                                    contentColor = MotoStockDs.colors.onErrorContainer
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Rechazar", style = MotoStockDs.typography.labelMedium)
                                }
                            }
                        }
                    }

                    EstadoCita.ACEPTADA -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = onPonerEnProceso,
                                modifier = Modifier.fillMaxWidth(),
                                shape = MotoStockDs.shapes.small,
                                contentPadding = PaddingValues(vertical = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MotoStockDs.colors.primary,
                                    contentColor = MotoStockDs.colors.onPrimary
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text("Iniciar Trabajo", style = MotoStockDs.typography.labelMedium)
                                }
                            }
                            Text(
                                text = "Cancelar cita",
                                style = MotoStockDs.typography.labelSmall,
                                color = MotoStockDs.colors.error,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clickable { onCancelar() }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    EstadoCita.EN_PROCESO -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = onFinalizar,
                                modifier = Modifier.fillMaxWidth(),
                                shape = MotoStockDs.shapes.small,
                                contentPadding = PaddingValues(vertical = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MotoStockDs.colors.secondary,
                                    contentColor = MotoStockDs.colors.onSecondary
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
                            Text(
                                text = "Cancelar cita",
                                style = MotoStockDs.typography.labelSmall,
                                color = MotoStockDs.colors.error,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clickable { onCancelar() }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    else -> {}
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
        containerColor = MotoStockDs.colors.surfaceContainerLowest,
        titleContentColor = MotoStockDs.colors.primary,
        title = { Text(title, style = MotoStockDs.typography.h3) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(message, style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant)
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.small
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(motivo) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MotoStockDs.colors.primary,
                    contentColor = MotoStockDs.colors.onPrimary
                ),
                shape = MotoStockDs.shapes.small,
                enabled = motivo.isNotBlank()
            ) {
                Text("Confirmar", style = MotoStockDs.typography.labelMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MotoStockDs.colors.secondary, style = MotoStockDs.typography.labelMedium)
            }
        }
    )
}

@Composable
private fun FinalizarServicioDialog(
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
                Text("Selecciona los productos utilizados:", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant)
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
                                Text(repuesto.nombre, style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurface)
                                Text("Stock: ${repuesto.cantidad} | S/. ${String.format("%.2f", repuesto.precioVenta)}", style = MotoStockDs.typography.bodySmall, color = MotoStockDs.colors.onSurfaceVariant)
                            }
                            if (cantidad > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (cantidad > 1) seleccionados[repuesto.id] = cantidad - 1 },
                                        modifier = Modifier.size(28.dp)
                                    ) { Text("-", style = MotoStockDs.typography.labelLarge) }
                                    Text(cantidad.toString(), style = MotoStockDs.typography.labelMedium, modifier = Modifier.padding(horizontal = 4.dp))
                                    IconButton(
                                        onClick = { if (cantidad < repuesto.cantidad) seleccionados[repuesto.id] = cantidad + 1 },
                                        modifier = Modifier.size(28.dp)
                                    ) { Text("+", style = MotoStockDs.typography.labelLarge) }
                                }
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = costo,
                    onValueChange = { costo = it },
                    label = { Text("Costo mano de obra / total (S/.)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.small
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val items = repuestosDisponibles.filter { seleccionados.containsKey(it.id) }
                        .map { it to (seleccionados[it.id] ?: 1) }
                    onConfirm(items, costo.toDoubleOrNull() ?: 0.0)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MotoStockDs.colors.secondary,
                    contentColor = MotoStockDs.colors.onSecondary
                ),
                shape = MotoStockDs.shapes.small
            ) { Text("Confirmar y Finalizar", style = MotoStockDs.typography.labelMedium) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MotoStockDs.colors.secondary, style = MotoStockDs.typography.labelMedium)
            }
        }
    )
}
