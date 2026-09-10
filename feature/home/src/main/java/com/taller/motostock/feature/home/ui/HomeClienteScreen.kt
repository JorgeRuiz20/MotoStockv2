package com.taller.motostock.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
        viewModel.uiEffect.collect {
            if (it is HomeContract.Effect.NavigateToLogin) {
                navController.navigate("login") { popUpTo(0) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MotoStockDs.colors.surface)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MotoStockDs.colors.surface)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MotoStockDs.colors.primary, shape = MotoStockDs.shapes.full),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MotoStockDs.colors.onPrimary
                        )
                    }
                    Column {
                        Text(
                            text = "Bienvenido de nuevo,",
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.onSurfaceVariant
                        )
                        Text(
                            text = if (state.nombre.isNotBlank()) state.nombre else "Cliente",
                            style = MotoStockDs.typography.h3,
                            color = MotoStockDs.colors.onSurface
                        )
                    }
                }
                IconButton(
                    onClick = { viewModel.onIntent(HomeContract.Intent.Logout) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MotoStockDs.colors.surfaceContainerHigh, shape = MotoStockDs.shapes.full)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = MotoStockDs.colors.onSurfaceVariant
                    )
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de citas del cliente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mis Citas",
                            style = MotoStockDs.typography.h3,
                            color = MotoStockDs.colors.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .background(MotoStockDs.colors.surfaceContainerHighest, shape = MotoStockDs.shapes.full)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${state.citasHoy} activas",
                                style = MotoStockDs.typography.labelSmall,
                                color = MotoStockDs.colors.primary
                            )
                        }
                    }
                    Text(
                        text = "Gestiona tus citas y revisa el historial de servicios de tu moto.",
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("agendar_cita") },
                            modifier = Modifier.weight(1f),
                            shape = MotoStockDs.shapes.small,
                            contentPadding = PaddingValues(vertical = 10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MotoStockDs.colors.primary,
                                contentColor = MotoStockDs.colors.onPrimary
                            )
                        ) {
                            Text("Agendar Cita", style = MotoStockDs.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = { navController.navigate("mis_servicios") },
                            modifier = Modifier.weight(1f),
                            shape = MotoStockDs.shapes.small,
                            contentPadding = PaddingValues(vertical = 10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MotoStockDs.colors.primary)
                        ) {
                            Text("Ver Historial", style = MotoStockDs.typography.labelMedium)
                        }
                    }
                }
            }

            // Quick Actions Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Agendar Cita",
                    subtitle = "Reserva taller para mantenimiento o reparación",
                    icon = Icons.Default.DateRange,
                    iconBgColor = MotoStockDs.colors.surfaceContainerHigh,
                    iconTintColor = MotoStockDs.colors.primary,
                    onClick = { navController.navigate("agendar_cita") }
                )
                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Mis Servicios",
                    subtitle = "Historial, repuestos y facturas anteriores",
                    icon = Icons.AutoMirrored.Filled.List,
                    iconBgColor = MotoStockDs.colors.surfaceContainerHigh,
                    iconTintColor = MotoStockDs.colors.secondary,
                    onClick = { navController.navigate("mis_servicios") }
                )
            }

            // Workshops Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MotoStockDs.colors.surfaceContainerHigh, shape = MotoStockDs.shapes.medium)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Taller Autorizado MotoStock",
                        style = MotoStockDs.typography.h3,
                        color = MotoStockDs.colors.onSurface
                    )
                    Text(
                        "Garantía oficial en repuestos originales y servicio certificado.",
                        style = MotoStockDs.typography.bodySmall,
                        color = MotoStockDs.colors.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MotoStockDs.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Av. Motores 120, Lima • Lunes a Sábado, 8:00 - 18:00",
                            style = MotoStockDs.typography.labelMedium,
                            color = MotoStockDs.colors.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, shape = MotoStockDs.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTintColor)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MotoStockDs.typography.h3, color = MotoStockDs.colors.onSurface)
                Text(subtitle, style = MotoStockDs.typography.bodySmall, color = MotoStockDs.colors.onSurfaceVariant)
            }
        }
    }
}
