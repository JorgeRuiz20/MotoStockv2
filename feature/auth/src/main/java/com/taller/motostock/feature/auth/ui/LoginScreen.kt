package com.taller.motostock.feature.auth.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.taller.motostock.core.domain.model.UserRole
import com.taller.motostock.feature.auth.AuthContract
import com.taller.motostock.feature.auth.viewmodel.LoginViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter

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

    LaunchedEffect(Unit) {
        if (viewModel.haySesionActiva()) {
            viewModel.onIntent(AuthContract.Intent.CargarRol)
        }
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
            .padding(MotoStockDs.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MotoStockDs.colors.primary
        )
        Text(text = "MotoStock Taller", style = MotoStockDs.typography.h1, color = MotoStockDs.colors.primary)
        Text(text = "Acceso de trabajadores", color = MotoStockDs.colors.secondary)
        
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.extraLarge))
        
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
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.medium))
        
        Button(
            onClick = { viewModel.onIntent(AuthContract.Intent.Login(email, password)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.secondary)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MotoStockDs.spacing.large),
                    color = MotoStockDs.colors.onPrimary
                )
            } else {
                Text("Ingresar")
            }
        }

        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))
        
        Button(
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    googleLauncher.launch(googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(MotoStockDs.spacing.medium),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(MotoStockDs.spacing.small))
            Text("Continuar con Google")
        }
        
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.medium))
        
        TextButton(onClick = { navController.navigate("register") }) {
            Text("¿Nuevo trabajador? Crear cuenta", style = MotoStockDs.typography.bodyLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
        }
    }
}
