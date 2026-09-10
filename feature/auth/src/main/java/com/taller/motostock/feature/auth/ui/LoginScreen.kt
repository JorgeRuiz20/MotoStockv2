package com.taller.motostock.feature.auth.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging
import com.taller.motostock.core.common.Constants
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.designsystem.MotoStockTextField
import com.taller.motostock.core.domain.model.UserRole
import com.taller.motostock.feature.auth.AuthContract
import com.taller.motostock.feature.auth.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.onIntent(AuthContract.Intent.LoginGoogle(idToken))
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In falló: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    // La pantalla de acceso requiere una acción explícita del usuario.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AuthContract.Effect.NavigateTo -> {
                    if (effect.role == UserRole.TRABAJADOR || effect.role == UserRole.ADMINISTRADOR) {
                        FirebaseMessaging.getInstance().subscribeToTopic("trabajadores")
                    }
                    val route = if (effect.role == UserRole.CLIENTE) "home_cliente" else "home"
                    navController.navigate(route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
                is AuthContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                AuthContract.Effect.NavigateToLogin -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoStockDs.colors.surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MotoStockDs.colors.primary, shape = MotoStockDs.shapes.large),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    style = MotoStockDs.typography.h1,
                    color = MotoStockDs.colors.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MotoStock",
                style = MotoStockDs.typography.h1,
                color = MotoStockDs.colors.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Gestión profesional de talleres de motocicletas",
                style = MotoStockDs.typography.bodyMedium,
                color = MotoStockDs.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Tarjeta de Login
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Iniciar Sesión",
                    style = MotoStockDs.typography.h3,
                    color = MotoStockDs.colors.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                MotoStockTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    placeholder = "correo@ejemplo.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                MotoStockTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    placeholder = "••••••••",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                // Botón Iniciar Sesión
                Button(
                    onClick = { viewModel.onIntent(AuthContract.Intent.Login(email, password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = !state.isLoading,
                    shape = MotoStockDs.shapes.medium,
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MotoStockDs.colors.primary,
                        contentColor = MotoStockDs.colors.onPrimary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MotoStockDs.colors.onPrimary
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("Iniciar Sesión", style = MotoStockDs.typography.labelLarge)
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MotoStockDs.colors.outlineVariant.copy(alpha = 0.3f)
                )

                // Botón Google Sign-In
                Button(
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleLauncher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MotoStockDs.shapes.medium,
                    contentPadding = PaddingValues(vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Icono "G" de Google con colores de la marca
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color.White, shape = MotoStockDs.shapes.full),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                style = MotoStockDs.typography.labelMedium,
                                color = Color(0xFF4285F4)
                            )
                        }
                        Text("Continuar con Google", style = MotoStockDs.typography.labelLarge, color = Color.White)
                    }
                }

                // Acceso rápido de prueba (solo en debug, visualmente discreto)
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        "Acceso rápido (solo pruebas):",
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { email = "cliente@motostock.com"; password = "123456" },
                            modifier = Modifier.weight(1f),
                            shape = MotoStockDs.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MotoStockDs.colors.surfaceContainerLow,
                                contentColor = MotoStockDs.colors.primary
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) { Text("Cliente", style = MotoStockDs.typography.labelSmall) }

                        Button(
                            onClick = { email = "trabajador@motostock.com"; password = "123456" },
                            modifier = Modifier.weight(1f),
                            shape = MotoStockDs.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MotoStockDs.colors.surfaceContainerLow,
                                contentColor = MotoStockDs.colors.primary
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) { Text("Trabajador", style = MotoStockDs.typography.labelSmall) }

                        Button(
                            onClick = { email = "admin@motostock.com"; password = "123456" },
                            modifier = Modifier.weight(1f),
                            shape = MotoStockDs.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MotoStockDs.colors.surfaceContainerLow,
                                contentColor = MotoStockDs.colors.primary
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) { Text("Admin", style = MotoStockDs.typography.labelSmall) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // No tienes cuenta
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "¿No tienes una cuenta?",
                style = MotoStockDs.typography.bodyMedium,
                color = MotoStockDs.colors.onSurfaceVariant
            )
            Text(
                "Regístrate",
                style = MotoStockDs.typography.labelLarge.copy(textDecoration = TextDecoration.Underline),
                color = MotoStockDs.colors.primary,
                modifier = Modifier.clickable { navController.navigate("register") }
            )
        }
    }
}
