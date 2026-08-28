package com.taller.motostock.feature.history.ui

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

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Nuevo Servicio", style = MotoStockDs.typography.h2) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MotoStockDs.colors.background,
                    titleContentColor = MotoStockDs.colors.primary
                )
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(MotoStockDs.spacing.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)
        ) {
            OutlinedTextField(value = placa, onValueChange = { placa = it.uppercase() }, label = { Text("Placa *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = problema, onValueChange = { problema = it }, label = { Text("Descripción del problema *") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = trabajo, onValueChange = { trabajo = it }, label = { Text("Trabajo realizado") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = km, onValueChange = { km = it }, label = { Text("Kilometraje") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = costo, onValueChange = { costo = it }, label = { Text("Costo mano de obra (S/.)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(value = tecnico, onValueChange = { tecnico = it }, label = { Text("Técnico asignado") }, modifier = Modifier.fillMaxWidth())

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = estado.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado del servicio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    EstadoServicio.entries.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.label) },
                            onClick = {
                                estado = item
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MotoStockDs.colors.primary)
                ) {
                    Text("Cancelar")
                }
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
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
