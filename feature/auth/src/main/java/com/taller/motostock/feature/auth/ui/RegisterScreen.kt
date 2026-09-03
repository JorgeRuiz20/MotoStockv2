package com.taller.motostock.feature.auth.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
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
            .background(MotoStockDs.colors.surface)
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp), // pt-8
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        // Encabezado con botón de atrás
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp) // mb-6
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
                text = "Crear Cuenta Nueva",
                style = MotoStockDs.typography.h3,
                color = MotoStockDs.colors.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Tarjeta de Formulario
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Input Nombre Completo
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nombre Completo", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                if (nombre.isEmpty()) Text("Juan Pérez", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Input Correo
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Correo Electrónico", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = email,
                        onValueChange = { email = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                if (email.isEmpty()) Text("juan@ejemplo.com", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Input Contraseña
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Contraseña", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = password,
                        onValueChange = { password = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                if (password.isEmpty()) Text("••••••••", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Input Confirmar Contraseña
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Confirmar Contraseña", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                if (confirmPassword.isEmpty()) Text("••••••••", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                // Botón Registrarse
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
                    shape = MotoStockDs.shapes.medium,
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.secondary, // En el HTML es bg-secondary
                        contentColor = MotoStockDs.colors.onSecondary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MotoStockDs.colors.onSecondary)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("Registrarme", style = MotoStockDs.typography.labelLarge)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ya tienes cuenta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "¿Ya tienes cuenta? ",
                style = MotoStockDs.typography.bodyMedium,
                color = MotoStockDs.colors.onSurfaceVariant
            )
            Text(
                "Inicia sesión",
                style = MotoStockDs.typography.labelLarge.copy(textDecoration = TextDecoration.Underline),
                color = MotoStockDs.colors.secondary,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }
    }
}
