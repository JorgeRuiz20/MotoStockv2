package com.taller.motostock.feature.client.ui

import android.app.TimePickerDialog
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
            .padding(MotoStockDs.spacing.medium)
    ) {
        Text(
            text = "Mis Servicios",
            style = MotoStockDs.typography.h3,
            color = MotoStockDs.colors.primary
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.medium))

        if (state.misCitas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(
                    text = "Aún no tienes citas registradas",
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f),
                    style = MotoStockDs.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                items(state.misCitas) { cita ->
                    CitaClienteItem(cita, onReagendar = { showReagendarDialog = cita })
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
            Text(
                text = "Moto: ${cita.modelo} (${cita.placa})",
                style = MotoStockDs.typography.h3,
                color = MotoStockDs.colors.primary
            )
            Text(
                text = "Fecha: ${fmt.format(Date(cita.fechaIngreso))} ${cita.horaIngreso}",
                style = MotoStockDs.typography.bodySmall,
                color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Servicio: ${cita.tipoServicio}",
                style = MotoStockDs.typography.bodyMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MotoStockDs.colors.onSurface
            )
            
            val statusColor = when (cita.estado) {
                EstadoCita.PENDIENTE -> Color(0xFFF57C00)
                EstadoCita.ACEPTADA, EstadoCita.FINALIZADO -> MotoStockDs.colors.success
                EstadoCita.RECHAZADA, EstadoCita.CANCELADA -> MotoStockDs.colors.error
                EstadoCita.EN_PROCESO -> MotoStockDs.colors.secondary
            }
            Text(
                text = "Estado: ${cita.estado.label}",
                color = statusColor,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                style = MotoStockDs.typography.bodyMedium
            )

            if (cita.motivoRechazo.isNotEmpty()) {
                Text(
                    text = "Motivo Rechazo: ${cita.motivoRechazo}",
                    color = MotoStockDs.colors.error,
                    style = MotoStockDs.typography.bodySmall
                )
            }
            if (cita.motivoCancelacion.isNotEmpty()) {
                Text(
                    text = "Motivo Cancelación: ${cita.motivoCancelacion}",
                    color = MotoStockDs.colors.error,
                    style = MotoStockDs.typography.bodySmall
                )
            }

            if (cita.estado == EstadoCita.RECHAZADA || cita.estado == EstadoCita.CANCELADA) {
                Button(
                    onClick = onReagendar,
                    modifier = Modifier.padding(top = MotoStockDs.spacing.small),
                    colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
                ) {
                    Text("Reagendar cita")
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
        title = { Text("Reagendar cita - ${cita.placa}", style = MotoStockDs.typography.h3) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                Text(
                    "Se enviará una nueva solicitud para la placa ${cita.placa}",
                    style = MotoStockDs.typography.bodyMedium
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción adicional") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(
                        text = "Hora: ${if (horaSeleccionada.isEmpty()) "No seleccionada" else horaSeleccionada}",
                        modifier = Modifier.weight(1f),
                        style = MotoStockDs.typography.bodyMedium
                    )
                    Button(
                        onClick = { timePickerDialog.show() },
                        colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.secondary)
                    ) {
                        Text("Seleccionar hora")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(descripcion, horaSeleccionada) },
                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
            ) { 
                Text("Reagendar") 
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { 
                Text("Cancelar", color = MotoStockDs.colors.secondary) 
            }
        }
    )
}
