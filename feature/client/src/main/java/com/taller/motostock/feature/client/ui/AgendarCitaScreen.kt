package com.taller.motostock.feature.client.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.feature.client.ClienteContract
import com.taller.motostock.feature.client.viewmodel.ClienteViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(
    navController: NavController,
    viewModel: ClienteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var placa by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ClienteContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    if (effect.message.contains("correctamente", ignoreCase = true)) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK", color = MotoStockDs.colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = MotoStockDs.colors.primary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MotoStockDs.colors.surfaceContainerLowest,
                titleContentColor = MotoStockDs.colors.primary,
                headlineContentColor = MotoStockDs.colors.primary,
                weekdayContentColor = MotoStockDs.colors.onSurfaceVariant,
                subheadContentColor = MotoStockDs.colors.onSurfaceVariant,
                yearContentColor = MotoStockDs.colors.onSurfaceVariant,
                currentYearContentColor = MotoStockDs.colors.primary,
                selectedYearContainerColor = MotoStockDs.colors.primary,
                selectedYearContentColor = MotoStockDs.colors.onPrimary,
                dayContentColor = MotoStockDs.colors.onSurface,
                selectedDayContainerColor = MotoStockDs.colors.primary,
                selectedDayContentColor = MotoStockDs.colors.onPrimary,
                todayContentColor = MotoStockDs.colors.primary,
                todayDateBorderColor = MotoStockDs.colors.primary
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoStockDs.colors.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = MotoStockDs.colors.primary)
            }
            Text(
                text = "Agendar Nueva Cita",
                style = MotoStockDs.typography.h3,
                color = MotoStockDs.colors.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Formulario
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                // Placa
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Placa de la Moto", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = placa,
                        onValueChange = { placa = it.uppercase() },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (placa.isEmpty()) Text("Ej. 4821-XYZ", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Motivo
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Motivo / Descripción del Problema", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        modifier = Modifier.height(100.dp),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (motivo.isEmpty()) Text("Describe brevemente ruidos, testigos encendidos o mantenimiento requerido...", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Fecha
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Fecha Preferida", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small)
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        val dateStr = selectedDateMillis?.let { 
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) 
                        } ?: "Selecciona una fecha"
                        
                        Text(dateStr, style = MotoStockDs.typography.bodyMedium, color = if (selectedDateMillis != null) MotoStockDs.colors.onSurface else MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (placa.isBlank() || motivo.isBlank() || selectedDateMillis == null) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // La hora no está en el mockup detallado más allá de un selector visual, enviaremos la fecha como está.
                        // (El backend se ajustará o solo se guardará la fecha)
                        val calActual = Calendar.getInstance().apply {
                            if (selectedDateMillis != null) {
                                timeInMillis = selectedDateMillis!!
                            }
                        }
                        val horaDeseada = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calActual.time)

                        viewModel.onIntent(ClienteContract.Intent.AgendarCita(placa, "Propietario Web", "123456789", "Desconocido", "Mantenimiento", motivo, horaDeseada))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    shape = MotoStockDs.shapes.medium,
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.secondary,
                        contentColor = MotoStockDs.colors.onSecondary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MotoStockDs.colors.onSecondary)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("Confirmar Cita", style = MotoStockDs.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
