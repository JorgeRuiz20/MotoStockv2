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
import com.taller.motostock.core.domain.model.UserRole
import com.taller.motostock.feature.home.HomeContract
import com.taller.motostock.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isAdmin = state.role == UserRole.ADMINISTRADOR

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
            text = "Panel de Control",
            style = MotoStockDs.typography.h1,
            color = MotoStockDs.colors.primary
        )
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.extraLarge))
        
        Button(
            onClick = { navController.navigate("inventario") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
        ) {
            Text("Inventario")
        }
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))
        
        Button(
            onClick = { navController.navigate("citas_trabajador") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
        ) {
            Text("Gestión de Citas")
        }
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))

        Button(
            onClick = { navController.navigate("historial") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
        ) {
            Text("Historial de Servicios")
        }
        Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))

        if (isAdmin) {
            Button(
                onClick = { navController.navigate("crear_trabajador") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.secondary)
            ) {
                Text("Crear Trabajador (Admin)")
            }
            Spacer(modifier = Modifier.height(MotoStockDs.spacing.small))
        }
        
        OutlinedButton(
            onClick = { viewModel.onIntent(HomeContract.Intent.Logout) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MotoStockDs.colors.primary)
        ) {
            Text("Cerrar Sesión")
        }
    }
}
