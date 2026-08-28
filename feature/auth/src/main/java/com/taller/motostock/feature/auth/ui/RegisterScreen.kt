package com.taller.motostock.feature.auth.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.feature.auth.AuthContract
import com.taller.motostock.feature.auth.viewmodel.LoginViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
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
                AuthContract.Effect.NavigateToLogin -> {
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is AuthContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MotoStockDs.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registro de Cliente",
            style = MotoStockDs.typography.h2,
            color = MotoStockDs.colors.primary
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.large))
        
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.large))
        
        Button(
            onClick = {
                if (password == confirmPassword) {
                    viewModel.onIntent(AuthContract.Intent.RegistrarCliente(email, password, nombre))
                } else {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MotoStockDs.spacing.large),
                    color = MotoStockDs.colors.onPrimary
                )
            } else {
                Text("Registrarse")
            }
        }
        
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Ya tengo cuenta, volver al login", color = MotoStockDs.colors.secondary)
        }
    }
}
