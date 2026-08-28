package com.taller.motostock.feature.client.ui

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.feature.client.ClienteContract
import com.taller.motostock.feature.client.viewmodel.ClienteViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(
    navController: NavController,
    viewModel: ClienteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var placa by remember { mutableStateOf("") }
    var propietario by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var tipoServicio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    
    val cal = Calendar.getInstance()
    var horaDeseada by remember { mutableStateOf("") }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            horaDeseada = String.format("%02d:%02d", hour, minute)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        true
    )

    val tipos = listOf("Mantenimiento", "Reparación", "Revisión", "Cambio de aceite", "Otro")
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            if (effect is ClienteContract.Effect.ShowMessage) {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                if (effect.message.contains("correctamente")) navController.popBackStack()
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
            text = "Agendar Cita",
            style = MotoStockDs.typography.h3,
            color = MotoStockDs.colors.primary
        )
        
        OutlinedTextField(value = placa, onValueChange = { placa = it.uppercase() }, label = { Text("Placa") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = propietario, onValueChange = { propietario = it }, label = { Text("Nombre del Propietario") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo de Moto") }, modifier = Modifier.fillMaxWidth())
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = tipoServicio,
                onValueChange = { tipoServicio = it },
                label = { Text("Tipo de Servicio") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                tipos.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            tipoServicio = selection
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = horaDeseada,
            onValueChange = {},
            label = { Text("Hora Deseada") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { timePickerDialog.show() }) {
                    Text("⏰")
                }
            }
        )

        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción del problema") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

        Spacer(modifier = Modifier.height(MotoStockDs.spacing.medium))
        
        Row(horizontalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MotoStockDs.colors.primary)
            ) {
                Text("Limpiar / Volver")
            }
            Button(
                onClick = {
                    if (placa.isBlank() || propietario.isBlank() || telefono.isBlank() || horaDeseada.isBlank()) {
                        Toast.makeText(context, "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.onIntent(ClienteContract.Intent.AgendarCita(placa, propietario, telefono, modelo, tipoServicio, descripcion, horaDeseada))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
            ) {
                Text("Agendar")
            }
        }
    }
}
