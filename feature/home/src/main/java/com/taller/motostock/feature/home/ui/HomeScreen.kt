package com.taller.motostock.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
                .background(MotoStockDs.colors.primary)
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
                            .background(MotoStockDs.colors.secondaryContainer, shape = MotoStockDs.shapes.full),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MotoStockDs.colors.onSecondaryContainer
                        )
                    }
                    Column {
                        Text(
                            text = "Bienvenido de nuevo,",
                            style = MotoStockDs.typography.bodySmall,
                            color = MotoStockDs.colors.primaryFixed
                        )
                        Text(
                            text = if (state.nombre.isNotBlank()) state.nombre
                                   else if (isAdmin) "Administrador" else "Mecánico",
                            style = MotoStockDs.typography.h3,
                            color = MotoStockDs.colors.onPrimary
                        )
                    }
                }
                IconButton(
                    onClick = { viewModel.onIntent(HomeContract.Intent.Logout) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MotoStockDs.colors.primaryContainer, shape = MotoStockDs.shapes.full)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = MotoStockDs.colors.primaryFixed
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MotoStockDs.colors.primary)
                }
            } else if (isAdmin) {
                // ── ADMIN DASHBOARD ──
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Citas Pendientes",
                        value = "${state.citasPendientes}",
                        valueColor = MotoStockDs.colors.primary,
                        subtitle = "Por atender"
                    )
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "En Proceso",
                        value = "${state.citasEnProceso}",
                        valueColor = MotoStockDs.colors.secondary,
                        subtitle = "En el taller ahora"
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Citas Activas",
                        value = "${state.citasHoy}",
                        valueStyle = MotoStockDs.typography.h3,
                        valueColor = MotoStockDs.colors.primary
                    )
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Repuestos en Stock",
                        value = "${state.repuestosTotal} ítems",
                        valueStyle = MotoStockDs.typography.h3,
                        valueColor = MotoStockDs.colors.secondary,
                        onClick = { navController.navigate("inventario") }
                    )
                }

                // Alerta de stock bajo (si hay)
                if (state.repuestosStockBajo > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3E0), shape = MotoStockDs.shapes.medium)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFF57C00),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${state.repuestosStockBajo} repuesto(s) con stock bajo. Revisar inventario.",
                                style = MotoStockDs.typography.bodySmall,
                                color = Color(0xFFF57C00)
                            )
                        }
                    }
                }

                // Sección Gestión de Taller
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gestión de Taller", style = MotoStockDs.typography.h3, color = MotoStockDs.colors.primary)
                    Button(
                        onClick = { navController.navigate("crear_trabajador") },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = MotoStockDs.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MotoStockDs.colors.secondary,
                            contentColor = MotoStockDs.colors.onSecondary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Crear Trabajador", style = MotoStockDs.typography.labelMedium)
                        }
                    }
                }

                // Accesos rápidos admin
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminQuickAction(
                        modifier = Modifier.weight(1f),
                        title = "Citas",
                        subtitle = "${state.citasHoy} activas",
                        onClick = { navController.navigate("citas_trabajador") }
                    )
                    AdminQuickAction(
                        modifier = Modifier.weight(1f),
                        title = "Inventario",
                        subtitle = "${state.repuestosTotal} repuestos",
                        onClick = { navController.navigate("inventario") }
                    )
                    AdminQuickAction(
                        modifier = Modifier.weight(1f),
                        title = "Historial",
                        subtitle = "Ver servicios",
                        onClick = { navController.navigate("historial") }
                    )
                }

            } else {
                // ── TRABAJADOR DASHBOARD ──
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "Citas Pendientes",
                        value = "${state.citasPendientes}",
                        valueColor = MotoStockDs.colors.primary,
                        subtitle = "Por aceptar"
                    )
                    DashboardCard(
                        modifier = Modifier.weight(1f),
                        title = "En Proceso",
                        value = "${state.citasEnProceso}",
                        valueColor = MotoStockDs.colors.secondary,
                        subtitle = "En el taller"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Citas Activas", style = MotoStockDs.typography.h3, color = MotoStockDs.colors.primary)
                    Text(
                        "Ver todas →",
                        style = MotoStockDs.typography.labelMedium,
                        color = MotoStockDs.colors.secondary,
                        modifier = Modifier.clickable { navController.navigate("citas_trabajador") }
                    )
                }

                // Card resumen de citas activas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
                        .padding(16.dp)
                ) {
                    if (state.citasHoy == 0) {
                        Text(
                            "No hay citas activas por el momento.",
                            style = MotoStockDs.typography.bodyMedium,
                            color = MotoStockDs.colors.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFF3E0), shape = MotoStockDs.shapes.full)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("${state.citasPendientes} pendientes", style = MotoStockDs.typography.labelSmall, color = Color(0xFFF57C00))
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE8F0FE), shape = MotoStockDs.shapes.full)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("${state.citasEnProceso} en proceso", style = MotoStockDs.typography.labelSmall, color = MotoStockDs.colors.primary)
                                }
                            }
                            Text(
                                "Total de citas activas: ${state.citasHoy}",
                                style = MotoStockDs.typography.bodyMedium,
                                color = MotoStockDs.colors.onSurface
                            )
                            Button(
                                onClick = { navController.navigate("citas_trabajador") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MotoStockDs.shapes.small,
                                contentPadding = PaddingValues(vertical = 10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MotoStockDs.colors.primary)
                            ) {
                                Text("Gestionar Citas", style = MotoStockDs.typography.labelMedium)
                            }
                        }
                    }
                }

                // Stock bajo (trabajador también lo ve)
                if (state.repuestosStockBajo > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3E0), shape = MotoStockDs.shapes.medium)
                            .padding(12.dp)
                            .clickable { navController.navigate("inventario") }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFF57C00),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${state.repuestosStockBajo} repuesto(s) con stock bajo. Ver inventario →",
                                style = MotoStockDs.typography.bodySmall,
                                color = Color(0xFFF57C00)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    valueColor: Color,
    valueStyle: TextStyle = MotoStockDs.typography.h1,
    subtitle: String? = null,
    subtitleColor: Color = MotoStockDs.colors.onSurfaceVariant,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, style = MotoStockDs.typography.bodySmall, color = MotoStockDs.colors.onSurfaceVariant)
            Text(text = value, style = valueStyle, color = valueColor, modifier = Modifier.padding(top = 4.dp))
            if (subtitle != null) {
                Text(text = subtitle, style = MotoStockDs.typography.bodySmall, color = subtitleColor, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun AdminQuickAction(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(MotoStockDs.colors.surfaceContainerLowest, shape = MotoStockDs.shapes.medium)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MotoStockDs.typography.labelLarge, color = MotoStockDs.colors.primary)
            Text(subtitle, style = MotoStockDs.typography.bodySmall, color = MotoStockDs.colors.onSurfaceVariant)
        }
    }
}
