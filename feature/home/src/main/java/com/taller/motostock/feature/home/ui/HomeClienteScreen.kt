package com.taller.motostock.feature.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.feature.home.HomeContract
import com.taller.motostock.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeClienteScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { if (it is HomeContract.Effect.NavigateToLogin) navController.navigate("login") { popUpTo(0) } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MotoStockDs.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido",
            style = MotoStockDs.typography.h1,
            color = MotoStockDs.colors.primary
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.extraLarge))
        
        Button(
            onClick = { navController.navigate("agendar_cita") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
        ) {
            Text("Agendar Cita")
        }
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))
        
        Button(
            onClick = { navController.navigate("mis_servicios") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.secondary)
        ) {
            Text("Mis Servicios / Historial")
        }
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.medium))
        
        OutlinedButton(
            onClick = { viewModel.onIntent(HomeContract.Intent.Logout) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MotoStockDs.colors.primary)
        ) {
            Text("Cerrar Sesión")
        }
    }
}
