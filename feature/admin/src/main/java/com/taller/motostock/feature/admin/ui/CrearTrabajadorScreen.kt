package com.taller.motostock.feature.admin.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
            .background(MotoStockDs.colors.surface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = MotoStockDs.colors.primary
                )
            }
            Text(
                text = "Nuevo Trabajador",
                style = MotoStockDs.typography.h3,
                color = MotoStockDs.colors.primary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Formulario
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, shape = MotoStockDs.shapes.medium)
                .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Nombre
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nombre y Apellidos", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (nombre.isEmpty()) Text("Ej. David Sánchez", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Correo
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Correo Electrónico", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = email,
                        onValueChange = { email = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (email.isEmpty()) Text("david@motostock.com", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Contraseña temporal
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Contraseña Temporal", style = MotoStockDs.typography.labelMedium, color = MotoStockDs.colors.onSurfaceVariant)
                    BasicTextField(
                        value = password,
                        onValueChange = { password = it },
                        textStyle = MotoStockDs.typography.bodyMedium.copy(color = MotoStockDs.colors.onSurface),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        cursorBrush = SolidColor(MotoStockDs.colors.primary),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (password.isEmpty()) Text("••••••••", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                // Confirmar contraseña
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
                            Box(modifier = Modifier.fillMaxWidth().background(MotoStockDs.colors.surfaceContainerLow, shape = MotoStockDs.shapes.small).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                if (confirmPassword.isEmpty()) Text("••••••••", style = MotoStockDs.typography.bodyMedium, color = MotoStockDs.colors.onSurfaceVariant.copy(alpha = 0.5f))
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Crear Cuenta
                Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            Toast.makeText(context, "Ingresa el nombre del trabajador", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (email.isBlank()) {
                            Toast.makeText(context, "Ingresa el correo electrónico", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (password != confirmPassword) {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.onIntent(AdminContract.Intent.CrearTrabajador(email, password, nombre))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    shape = MotoStockDs.shapes.medium,
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.secondary,
                        contentColor = MotoStockDs.colors.onSecondary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MotoStockDs.colors.onSecondary
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text("Crear Cuenta", style = MotoStockDs.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
