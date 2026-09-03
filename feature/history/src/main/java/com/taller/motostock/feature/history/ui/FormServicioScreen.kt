package com.taller.motostock.feature.history.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.domain.model.EstadoServicio
import com.taller.motostock.core.domain.model.ServicioHistorial
import com.taller.motostock.feature.history.HistorialContract
import com.taller.motostock.feature.history.viewmodel.HistorialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormServicioScreen(
    navController: NavController,
    viewModel: HistorialViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var placa by remember { mutableStateOf("") }
    var problema by remember { mutableStateOf("") }
    var trabajo by remember { mutableStateOf("") }
    var km by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var tecnico by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(EstadoServicio.EN_PROCESO) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            if (effect is HistorialContract.Effect.ShowMessage) {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                if (effect.message.contains("correctamente")) navController.popBackStack()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoStockDs.colors.surface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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
                text = "Orden de Trabajo",
                style = MotoStockDs.typography.h3,
                color = MotoStockDs.colors.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Form Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, shape = MotoStockDs.shapes.medium)
                .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Resumen Vehículo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (placa.isNotBlank()) "Vehículo: $placa" else "Nueva Orden de Trabajo",
                                style = MotoStockDs.typography.labelLarge,
                                color = MotoStockDs.colors.primary
                            )
                            Text(
                                text = "Estado: ${estado.label}",
                                style = MotoStockDs.typography.bodySmall,
                                color = MotoStockDs.colors.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(MotoStockDs.colors.infoContainer, shape = MotoStockDs.shapes.full)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("En Taller", style = MotoStockDs.typography.labelSmall, color = MotoStockDs.colors.primary)
                        }
                    }
                }

                // Placa
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Placa del Vehículo *", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
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

                // Problema reportado
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Problema Reportado *", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = problema,
                        onValueChange = { problema = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (problema.isEmpty()) Text("Revisión 10,000km y ajuste general...", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Trabajo realizado
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Trabajo Realizado", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = trabajo,
                        onValueChange = { trabajo = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        modifier = Modifier.height(72.dp),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (trabajo.isEmpty()) Text("Detalle de labores realizadas...", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Grid: Kilometraje & Mano de Obra
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Kilometraje Actual", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                        BasicTextField(
                            value = km,
                            onValueChange = { km = it },
                            textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush = SolidColor(MotoStockDs.colors.primary),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                    if (km.isEmpty()) Text("Ej. 10240", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Mano de Obra (S/.)", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                        BasicTextField(
                            value = costo,
                            onValueChange = { costo = it },
                            textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            cursorBrush = SolidColor(MotoStockDs.colors.primary),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                    if (costo.isEmpty()) Text("0.00", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                // Técnico Asignado
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Técnico Asignado", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = tecnico,
                        onValueChange = { tecnico = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (tecnico.isEmpty()) Text("Nombre del mecánico", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Estado del Servicio Dropdown
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Estado del Servicio", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = estado.label,
                                style = MotoStockDs.typography.bodyMedium,
                                color = MotoStockDs.colors.onSurface
                            )
                        }
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(MotoStockDs.colors.surfaceContainerLowest)
                        ) {
                            EstadoServicio.entries.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.label, style = MotoStockDs.typography.bodyMedium) },
                                    onClick = {
                                        estado = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Caja de Costo Total Automático
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MotoStockDs.colors.primaryContainer, shape = MotoStockDs.shapes.medium)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Costo Total Mano de Obra",
                                style = MotoStockDs.typography.bodySmall,
                                color = MotoStockDs.colors.primaryFixed
                            )
                            val monto = costo.toDoubleOrNull() ?: 0.0
                            Text(
                                text = "S/. ${String.format("%.2f", monto)}",
                                style = MotoStockDs.typography.h2,
                                color = MotoStockDs.colors.onPrimary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MotoStockDs.colors.secondaryContainer
                        )
                    }
                }

                // Botón Guardar
                Button(
                    onClick = {
                        if (placa.isBlank() || problema.isBlank()) {
                            Toast.makeText(context, "Placa y problema son requeridos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val ahora = System.currentTimeMillis()
                        val fechaSalida = if (estado == EstadoServicio.LISTO || estado == EstadoServicio.ENTREGADO) ahora else null
                        val servicio = ServicioHistorial(
                            placa = placa,
                            descripcionProblema = problema,
                            trabajoRealizado = trabajo,
                            kilometraje = km.toIntOrNull() ?: 0,
                            costoManoObra = costo.toDoubleOrNull() ?: 0.0,
                            tecnico = tecnico,
                            estado = estado,
                            fechaIngreso = ahora,
                            fechaSalida = fechaSalida
                        )
                        viewModel.onIntent(HistorialContract.Intent.GuardarServicio(servicio))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.medium,
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.secondary,
                        contentColor = MotoStockDs.colors.onSecondary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Finalizar y Guardar Servicio", style = MotoStockDs.typography.labelLarge)
                    }
                }
            }
        }
    }
}
