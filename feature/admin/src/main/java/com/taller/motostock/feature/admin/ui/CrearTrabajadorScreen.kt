package com.taller.motostock.feature.admin.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.feature.admin.AdminContract
import com.taller.motostock.feature.admin.viewmodel.AdminViewModel

@Composable
fun CrearTrabajadorScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                AdminContract.Effect.NavigateToLogin -> {
                    Toast.makeText(context, "Trabajador creado exitosamente. La sesión fue cerrada por seguridad.", Toast.LENGTH_LONG).show()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
                is AdminContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MotoStockDs.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MotoStockDs.spacing.medium)
    ) {
        Text(
            text = "Crear cuenta de trabajador",
            style = MotoStockDs.typography.h2,
            color = MotoStockDs.colors.primary
        )
        Text(
            text = "Solo el administrador puede realizar esta acción",
            color = MotoStockDs.colors.secondary,
            style = MotoStockDs.typography.bodyMedium
        )
        
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del trabajador") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña temporal") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(vertical = MotoStockDs.spacing.medium),
                color = MotoStockDs.colors.primary
            )
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
                    if (nombre.isBlank()) {
                        Toast.makeText(context, "Ingresa el nombre", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.onIntent(AdminContract.Intent.CrearTrabajador(email, password, nombre))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary),
                enabled = !state.isLoading
            ) {
                Text("Crear")
            }
        }
    }
}
