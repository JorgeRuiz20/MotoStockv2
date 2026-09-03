package com.taller.motostock.feature.admin.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.designsystem.MotoStockCard
import com.taller.motostock.core.designsystem.MotoStockTextField
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
                AdminContract.Effect.NavigateBackToDashboard -> {
                    Toast.makeText(context, "Trabajador creado exitosamente.", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
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

        Text(
            text = "Crea el acceso del nuevo miembro del taller.",
            style = MotoStockDs.typography.bodyMedium,
            color = MotoStockDs.colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Formulario
        MotoStockCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MotoStockTextField(nombre, { nombre = it }, "Nombre y apellidos", placeholder = "Ej. David Sánchez")
                MotoStockTextField(email, { email = it }, "Correo electrónico", placeholder = "david@motostock.com", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                MotoStockTextField(password, { password = it }, "Contraseña temporal", placeholder = "••••••••", visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                MotoStockTextField(confirmPassword, { confirmPassword = it }, "Confirmar contraseña", placeholder = "••••••••", visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))

                state.error?.let { error ->
                    Text(
                        text = error,
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onErrorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MotoStockDs.colors.errorContainer, shape = MotoStockDs.shapes.small)
                            .padding(12.dp)
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
