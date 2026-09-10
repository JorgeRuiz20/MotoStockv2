package com.taller.motostock.feature.registration.ui

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
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
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.core.domain.model.EstadoCita
import com.taller.motostock.feature.registration.RegistroVehicularContract
import com.taller.motostock.feature.registration.viewmodel.RegistroVehicularViewModel
import java.util.*

@Composable
fun RegistroVehicularScreen(
    navController: NavController,
    viewModel: RegistroVehicularViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var placa by remember { mutableStateOf("") }
    var propietario by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var tipoServicio by remember { mutableStateOf("") }

    val cal = Calendar.getInstance()
    var horaSeleccionada by remember {
        mutableStateOf(String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)))
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            horaSeleccionada = String.format("%02d:%02d", hour, minute)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        true
    )

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            if (effect is RegistroVehicularContract.Effect.ShowMessage) {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                if (effect.message.contains("correctamente")) {
                    placa = ""; propietario = ""; telefono = ""; modelo = ""; tipoServicio = ""
                }
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
                    text = "Registro Vehicular",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.onSurface,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Button(
                onClick = { navController.navigate("en_taller") },
                shape = MotoStockDs.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MotoStockDs.colors.surfaceContainerHighest,
                    contentColor = MotoStockDs.colors.onSurface
                )
            ) {
                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("En taller", style = MotoStockDs.typography.labelSmall)
            }
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
                Text(
                    text = "Ingreso de Motocicleta",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.primary
                )

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

                // Modelo
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Modelo de Moto", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = modelo,
                        onValueChange = { modelo = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (modelo.isEmpty()) Text("Ej. Yamaha MT-07", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Propietario
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nombre del Propietario", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = propietario,
                        onValueChange = { propietario = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (propietario.isEmpty()) Text("Ej. Carlos Mendoza", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Teléfono
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Teléfono de Contacto", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (telefono.isEmpty()) Text("+51 900 000 000", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Tipo de Servicio
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tipo de Servicio", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = tipoServicio,
                        onValueChange = { tipoServicio = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (tipoServicio.isEmpty()) Text("Ej. Mantenimiento 10,000km", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Hora de ingreso
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hora de ingreso: $horaSeleccionada",
                        style = MotoStockDs.typography.bodyMedium,
                        color = MotoStockDs.colors.onSurface
                    )
                    Button(
                        onClick = { timePickerDialog.show() },
                        shape = MotoStockDs.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MotoStockDs.colors.primaryContainer,
                            contentColor = MotoStockDs.colors.onPrimaryContainer
                        )
                    ) {
                        Text("Cambiar", style = MotoStockDs.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Registrar Ingreso
                Button(
                    onClick = {
                        if (placa.isBlank()) {
                            Toast.makeText(context, "La placa es requerida", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val cita = Cita(
                            placa = placa,
                            propietario = propietario,
                            telefono = telefono,
                            modelo = modelo,
                            fechaIngreso = System.currentTimeMillis(),
                            horaIngreso = horaSeleccionada,
                            tipoServicio = tipoServicio,
                            estado = EstadoCita.EN_PROCESO
                        )
                        viewModel.onIntent(RegistroVehicularContract.Intent.RegistrarIngreso(cita))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.medium,
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.primary,
                        contentColor = MotoStockDs.colors.onPrimary
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
                        Text("Registrar Ingreso al Taller", style = MotoStockDs.typography.labelLarge)
                    }
                }
            }
        }
    }
}
