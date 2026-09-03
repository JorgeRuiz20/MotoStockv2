package com.taller.motostock.feature.client.ui

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.taller.motostock.feature.client.ClienteContract
import com.taller.motostock.feature.client.viewmodel.ClienteViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MisServiciosScreen(
    navController: NavController,
    viewModel: ClienteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showReagendarDialog by remember { mutableStateOf<Cita?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            if (effect is ClienteContract.Effect.ShowMessage) {
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
                text = "Historial de Servicios",
                style = MotoStockDs.typography.h3,
                color = MotoStockDs.colors.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        if (state.misCitas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes citas registradas",
                    color = MotoStockDs.colors.onSurfaceVariant,
                    style = MotoStockDs.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.misCitas) { cita ->
                    CitaClienteItem(
                        cita = cita,
                        onReagendar = { showReagendarDialog = cita }
                    )
                }
            }
        }
    }

    if (showReagendarDialog != null) {
        ReagendarDialog(
            cita = showReagendarDialog!!,
            onDismiss = { showReagendarDialog = null },
            onConfirm = { desc, hora ->
                viewModel.onIntent(ClienteContract.Intent.ReagendarCita(showReagendarDialog!!, desc, hora))
                showReagendarDialog = null
            }
        )
    }
}

@Composable
fun CitaClienteItem(cita: Cita, onReagendar: () -> Unit) {
    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val statusBg = when (cita.estado) {
        EstadoCita.FINALIZADO -> MotoStockDs.colors.successContainer
        EstadoCita.PENDIENTE -> MotoStockDs.colors.warningContainer
        EstadoCita.EN_PROCESO, EstadoCita.ACEPTADA -> MotoStockDs.colors.infoContainer
        EstadoCita.RECHAZADA, EstadoCita.CANCELADA -> MotoStockDs.colors.errorContainer
    }

    val statusText = when (cita.estado) {
        EstadoCita.FINALIZADO -> MotoStockDs.colors.success
        EstadoCita.PENDIENTE -> MotoStockDs.colors.onWarningContainer
        EstadoCita.EN_PROCESO, EstadoCita.ACEPTADA -> MotoStockDs.colors.primary
        EstadoCita.RECHAZADA, EstadoCita.CANCELADA -> MotoStockDs.colors.onErrorContainer
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = MotoStockDs.shapes.medium)
            .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header: Badge de estado y Fecha
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (cita.estado == EstadoCita.FINALIZADO) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = statusText
                            )
                        }
                        Text(
                            text = cita.estado.label,
                            style = MotoStockDs.typography.labelSmall,
                            color = statusText
                        )
                    }
                }

                Text(
                    text = fmt.format(Date(cita.fechaIngreso)),
                    style = MotoStockDs.typography.bodySmall,
                    color = MotoStockDs.colors.onSurfaceVariant
                )
            }

            // Título, Moto y Costo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = cita.tipoServicio.ifEmpty { "Mantenimiento General" },
                        style = MotoStockDs.typography.h3,
                        color = MotoStockDs.colors.primary
                    )
                    Text(
                        text = "${cita.modelo.ifEmpty { "Moto" }} • ${cita.placa}",
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                }

                if (cita.costoServicio > 0) {
                    Text(
                        text = "S/. ${String.format("%.2f", cita.costoServicio)}",
                        style = MotoStockDs.typography.h2,
                        color = MotoStockDs.colors.secondary
                    )
                }
            }

            // Contenedor de detalles
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small)
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (cita.descripcion.isNotBlank()) {
                        Text(
                            text = "Descripción: ${cita.descripcion}",
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.onSurfaceVariant
                        )
                    }
                    if (cita.horaDeseada.isNotBlank()) {
                        Text(
                            text = "Horario preferido: ${cita.horaDeseada}",
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.onSurfaceVariant
                        )
                    }
                }
            }

            val motivo = when (cita.estado) {
                EstadoCita.RECHAZADA -> cita.motivoRechazo
                EstadoCita.CANCELADA -> cita.motivoCancelacion
                else -> ""
            }
            if (motivo.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MotoStockDs.colors.errorContainer.copy(alpha = 0.55f), shape = MotoStockDs.shapes.small)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = if (cita.estado == EstadoCita.RECHAZADA) "Motivo del rechazo" else "Motivo de la cancelación",
                            style = MotoStockDs.typography.labelMedium,
                            color = MotoStockDs.colors.onErrorContainer
                        )
                        Text(
                            text = motivo,
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.onErrorContainer
                        )
                    }
                }
            }

            // Botón Reagendar si fue rechazada o cancelada
            if (cita.estado == EstadoCita.RECHAZADA || cita.estado == EstadoCita.CANCELADA) {
                Button(
                    onClick = onReagendar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.primary,
                        contentColor = MotoStockDs.colors.onPrimary
                    )
                ) {
                    Text("Solicitar nueva fecha", style = MotoStockDs.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun ReagendarDialog(
    cita: Cita,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var descripcion by remember { mutableStateOf(cita.descripcion) }
    var horaSeleccionada by remember { mutableStateOf("") }
    val context = LocalContext.current
    val cal = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            horaSeleccionada = String.format("%02d:%02d", hour, minute)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MotoStockDs.colors.surfaceContainerLowest,
        titleContentColor = MotoStockDs.colors.primary,
        title = { Text("Reagendar cita - ${cita.placa}", style = MotoStockDs.typography.h3) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Se enviará una nueva solicitud para la placa ${cita.placa}",
                    style = MotoStockDs.typography.bodyMedium,
                    color = MotoStockDs.colors.onSurfaceVariant
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción adicional") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.small
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Hora: ${if (horaSeleccionada.isEmpty()) "Sin seleccionar" else horaSeleccionada}",
                        style = MotoStockDs.typography.bodyMedium,
                        color = MotoStockDs.colors.onSurface
                    )
                    Button(
                        onClick = { timePickerDialog.show() },
                        shape = MotoStockDs.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MotoStockDs.colors.secondaryContainer,
                            contentColor = MotoStockDs.colors.onSecondaryContainer
                        )
                    ) {
                        Text("Hora", style = MotoStockDs.typography.labelSmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(descripcion, horaSeleccionada) },
                shape = MotoStockDs.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MotoStockDs.colors.primary,
                    contentColor = MotoStockDs.colors.onPrimary
                )
            ) {
                Text("Reagendar", style = MotoStockDs.typography.labelMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MotoStockDs.colors.secondary, style = MotoStockDs.typography.labelMedium)
            }
        }
    )
}
