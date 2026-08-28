package com.taller.motostock.feature.history.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.domain.model.Cita
import com.taller.motostock.feature.history.AtencionesHistorialContract
import com.taller.motostock.feature.history.viewmodel.AtencionesHistorialViewModel
import org.json.JSONArray
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun HistorialScreen(
    navController: NavController,
    viewModel: AtencionesHistorialViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var filterLabel by remember { mutableStateOf("") }

    val cal = Calendar.getInstance()
    val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AtencionesHistorialContract.Effect.ShowMessage -> {
                    // Aquí podrías usar un SnackbarHostState si estuviera disponible en el Scaffold
                    android.widget.Toast.makeText(context, effect.message, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MotoStockDs.spacing.medium)
    ) {
        Text(
            text = "Historial de atenciones",
            style = MotoStockDs.typography.h3,
            color = MotoStockDs.colors.primary
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar por placa") },
                modifier = Modifier.weight(1f),
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            Button(
                onClick = { viewModel.onIntent(AtencionesHistorialContract.Intent.BuscarPorPlaca(query)) },
                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
            ) {
                Text("Buscar")
            }
        }
        
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)
        ) {
            Button(
                onClick = {
                    DatePickerDialog(context, { _, y, m, d ->
                        cal.set(y, m, d, 0, 0, 0)
                        val inicio = cal.timeInMillis
                        val inicioStr = fmt.format(cal.time)

                        DatePickerDialog(context, { _, y2, m2, d2 ->
                            val cal2 = Calendar.getInstance()
                            cal2.set(y2, m2, d2, 23, 59, 59)
                            val fin = cal2.timeInMillis
                            val finStr = fmt.format(cal2.time)

                            filterLabel = "$inicioStr - $finStr"
                            viewModel.onIntent(AtencionesHistorialContract.Intent.FiltrarPorFecha(inicio, fin))
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.secondary)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(MotoStockDs.spacing.extraSmall))
                Text("Filtrar por fecha", style = MotoStockDs.typography.labelSmall)
            }
            
            if (filterLabel.isNotEmpty()) {
                TextButton(onClick = {
                    filterLabel = ""
                    query = ""
                    viewModel.onIntent(AtencionesHistorialContract.Intent.LimpiarFiltro)
                }) {
                    Text("✖ Limpiar", color = MotoStockDs.colors.error)
                }
            }
        }

        if (filterLabel.isNotEmpty()) {
            Text(
                text = "Filtro: $filterLabel",
                style = MotoStockDs.typography.bodySmall,
                color = MotoStockDs.colors.primary,
                modifier = Modifier.padding(vertical = MotoStockDs.spacing.extraSmall)
            )
        }

        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))

        if (state.atenciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Sin atenciones finalizadas",
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f),
                    style = MotoStockDs.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.small)) {
                items(state.atenciones) { cita ->
                    HistorialItem(cita)
                }
            }
        }
    }
}

data class RepuestoDisplay(val nombre: String, val cantidad: Int, val precio: Double)

@Composable
fun HistorialItem(cita: Cita) {
    val fmtFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currency = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    val repuestosList = remember(cita.repuestosUsadosJson) {
        val list = mutableListOf<RepuestoDisplay>()
        if (cita.repuestosUsadosJson.isNotBlank()) {
            try {
                val jsonArray = JSONArray(cita.repuestosUsadosJson)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        RepuestoDisplay(
                            nombre = obj.getString("nombre"),
                            cantidad = obj.getInt("cantidad"),
                            precio = obj.getDouble("precioUnitario")
                        )
                    )
                }
            } catch (e: Exception) {
                // Error parsing JSON
            }
        }
        list
    }

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
                    text = "✓ FINALIZADO",
                    style = MotoStockDs.typography.labelMedium,
                    color = MotoStockDs.colors.success,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            Text(
                text = cita.propietario.ifEmpty { "Sin nombre" },
                style = MotoStockDs.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MotoStockDs.colors.onSurface
            )
            Text(
                text = "Modelo: ${cita.modelo.ifEmpty { "No especificado" }}",
                color = MotoStockDs.colors.onSurface.copy(alpha = 0.7f),
                style = MotoStockDs.typography.bodySmall
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
                color = MotoStockDs.colors.primary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            if (cita.descripcion.isNotBlank()) {
                Text(
                    text = cita.descripcion,
                    style = MotoStockDs.typography.bodySmall,
                    color = MotoStockDs.colors.onSurface
                )
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = MotoStockDs.spacing.small),
                color = MotoStockDs.colors.onSurface.copy(alpha = 0.12f)
            )
            
            Text(
                text = "🟢 Ingreso: ${fmtFecha.format(Date(cita.fechaIngreso))} ${cita.horaIngreso}",
                style = MotoStockDs.typography.labelSmall,
                color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f)
            )
            val fechaSalida = cita.fechaSalida
            if (fechaSalida != null) {
                Text(
                    text = "🔴 Salida: ${fmtFecha.format(Date(fechaSalida))} ${cita.horaSalida}",
                    style = MotoStockDs.typography.labelSmall,
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f)
                )
            }

            if (repuestosList.isNotEmpty() || cita.costoServicio > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = MotoStockDs.spacing.small),
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.12f)
                )
                Text(
                    text = "🛠️ Productos utilizados:",
                    style = MotoStockDs.typography.labelMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MotoStockDs.colors.primary
                )
                
                repuestosList.forEach { item ->
                    Text(
                        text = "• ${item.nombre} x${item.cantidad} (${currency.format(item.precio)} c/u)",
                        style = MotoStockDs.typography.labelSmall,
                        color = MotoStockDs.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Text(
                    text = "💰 Costo total: ${currency.format(cita.costoServicio)}",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.primary,
                    modifier = Modifier.padding(top = MotoStockDs.spacing.extraSmall)
                )
            }
        }
    }
}
