package com.taller.motostock.feature.client.ui

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.feature.client.ClienteContract
import com.taller.motostock.feature.client.viewmodel.ClienteViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AgendarCitaScreen(
    navController: NavController,
    viewModel: ClienteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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

    AgendarCitaContent(
        state = state,
        onBackClick = { navController.popBackStack() },
        onSubmitCita = { placa, motivo, selectedDateMillis ->
            val calActual = Calendar.getInstance().apply {
                timeInMillis = selectedDateMillis
            }
            val horaDeseada = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calActual.time)
            viewModel.onIntent(
                ClienteContract.Intent.AgendarCita(
                    placa = placa,
                    propietario = "Propietario Web",
                    telefono = "123456789",
                    modelo = "Desconocido",
                    tipoServicio = "Mantenimiento",
                    descripcion = motivo,
                    horaDeseada = horaDeseada
                )
            )
        },
        formatDate = { millis ->
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
        },
        onValidationError = { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    )
}
