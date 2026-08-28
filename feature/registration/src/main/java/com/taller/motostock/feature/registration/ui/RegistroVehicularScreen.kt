package com.taller.motostock.feature.registration.ui

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.text.SimpleDateFormat
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
            .padding(MotoStockDs.spacing.medium)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)
    ) {
        Text(
            text = "Registro Vehicular",
            style = MotoStockDs.typography.h3,
            color = MotoStockDs.colors.primary
        )
        
        Button(
            onClick = { navController.navigate("en_taller") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.secondary)
        ) {
            Icon(Icons.Default.Build, contentDescription = null)
            Spacer(modifier = Modifier.width(MotoStockDs.spacing.small))
            Text("Ver vehículos en atención")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(MotoStockDs.elevation.small),
            shape = MotoStockDs.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(MotoStockDs.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)
            ) {
                Text(
                    text = "Datos del vehículo",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.primary
                )
                
                OutlinedTextField(value = placa, onValueChange = { placa = it.uppercase() }, label = { Text("Placa *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = propietario, onValueChange = { propietario = it }, label = { Text("Nombre del propietario") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tipoServicio, onValueChange = { tipoServicio = it }, label = { Text("Tipo de servicio") }, modifier = Modifier.fillMaxWidth())
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hora: $horaSeleccionada",
                        style = MotoStockDs.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        color = MotoStockDs.colors.onSurface
                    )
                    Button(
                        onClick = { timePickerDialog.show() },
                        colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.secondary)
                    ) {
                        Text("Cambiar hora")
                    }
                }

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
                    colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
                ) {
                    Text("Registrar ingreso al taller")
                }
            }
        }
    }
}
