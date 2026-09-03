package com.taller.motostock.feature.history.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AtencionesHistorialContract.Effect.ShowMessage -> {
                    android.widget.Toast.makeText(context, effect.message, android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoStockDs.colors.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp)
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
                text = "Historial de Servicios",
                style = MotoStockDs.typography.h3,
                color = MotoStockDs.colors.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Filtro Simplificado para el UI HTML, que solo muestra listado limpio.
        // Ocultaré el input nativo de placa para acercarme a "#view-servicios" que asume un cliente
        // Si quieres que el trabajador siga buscando, lo dejamos
        /*Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
           // BasicTextField si se quiere buscar...
        }*/

        if (state.atenciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Sin atenciones finalizadas",
                    color = MotoStockDs.colors.onSurface.copy(alpha = 0.6f),
                    style = MotoStockDs.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
    val fmtFecha = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
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
            }
        }
        list
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            
            // Etiqueta y Fecha
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.background(MotoStockDs.colors.successContainer, shape = MotoStockDs.shapes.full).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = MotoStockDs.colors.success)
                        Text("Finalizado", style = MotoStockDs.typography.labelSmall, color = MotoStockDs.colors.success)
                    }
                }
                Text(
                    text = cita.fechaSalida?.let { fmtFecha.format(Date(it)) } ?: fmtFecha.format(Date(cita.fechaIngreso)), 
                    style = MotoStockDs.typography.bodySmall, 
                    color = MotoStockDs.colors.onSurfaceVariant
                )
            }
            
            // Título, Modelo, Precio
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(text = cita.tipoServicio.ifEmpty { "Servicio de Mantenimiento" }, style = MotoStockDs.typography.h3, color = MotoStockDs.colors.primary)
                    Text(
                        text = "${cita.modelo.ifEmpty { "Desconocido" }} • ${cita.placa}",
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                }
                Text(
                    text = currency.format(cita.costoServicio),
                    style = MotoStockDs.typography.h2,
                    color = MotoStockDs.colors.secondary
                )
            }
            
            // Detalles Repuestos / Mano de obra
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small)
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (repuestosList.isNotEmpty()) {
                        val nombres = repuestosList.joinToString(", ") { it.nombre }
                        Text(
                            text = "Repuestos:",
                            style = MotoStockDs.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MotoStockDs.colors.onSurfaceVariant
                        )
                        Text(text = nombres, style = MotoStockDs.typography.bodySmall, color = MotoStockDs.colors.onSurfaceVariant)
                    } else {
                        Text("Sin repuestos registrados", style = MotoStockDs.typography.bodySmall, color = MotoStockDs.colors.onSurfaceVariant)
                    }
                    
                    if (cita.descripcion.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Descripción:",
                            style = MotoStockDs.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MotoStockDs.colors.onSurfaceVariant
                        )
                        Text(text = cita.descripcion, style = MotoStockDs.typography.bodySmall, color = MotoStockDs.colors.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
