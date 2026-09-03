package com.taller.motostock.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taller.motostock.core.designsystem.MotoStockDs
import com.taller.motostock.core.domain.model.UserRole
import com.taller.motostock.feature.admin.ui.CrearTrabajadorScreen
import com.taller.motostock.feature.appointments.ui.CitasTrabajadorScreen
import com.taller.motostock.feature.auth.ui.LoginScreen
import com.taller.motostock.feature.auth.ui.RegisterScreen
import com.taller.motostock.feature.client.ui.AgendarCitaScreen
import com.taller.motostock.feature.client.ui.MisServiciosScreen
import com.taller.motostock.feature.history.ui.FormServicioScreen
import com.taller.motostock.feature.history.ui.HistorialScreen
import com.taller.motostock.feature.home.ui.HomeClienteScreen
import com.taller.motostock.feature.home.ui.HomeScreen
import com.taller.motostock.feature.inventory.ui.FormRepuestoScreen
import com.taller.motostock.feature.inventory.ui.InventarioScreen
import com.taller.motostock.feature.registration.ui.EnTallerScreen
import com.taller.motostock.feature.registration.ui.RegistroVehicularScreen

// ─── Items por rol ────────────────────────────────────────────────────────────

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    // Trabajador / Admin
    object Home       : BottomNavItem("home",             "Inicio",     Icons.Default.Home)
    object Citas      : BottomNavItem("citas_trabajador", "Citas",      Icons.Default.DateRange)
    object Inventario : BottomNavItem("inventario",       "Inventario", Icons.Default.ShoppingCart)
    object Historial  : BottomNavItem("historial",        "Historial",  Icons.Default.List)

    // Cliente
    object HomeCliente  : BottomNavItem("home_cliente",    "Inicio",     Icons.Default.Home)
    object Agendar      : BottomNavItem("agendar_cita",    "Agendar",    Icons.Default.DateRange)
    object MisServicios : BottomNavItem("mis_servicios",   "Servicios",  Icons.Default.List)
}

private val WORKER_TABS = listOf(
    BottomNavItem.Home,
    BottomNavItem.Citas,
    BottomNavItem.Inventario,
    BottomNavItem.Historial
)

private val CLIENT_TABS = listOf(
    BottomNavItem.HomeCliente,
    BottomNavItem.Agendar,
    BottomNavItem.MisServicios
)

private val WORKER_ROUTES  = WORKER_TABS.map { it.route } + listOf("registro_vehicular")
private val CLIENT_ROUTES  = CLIENT_TABS.map { it.route }

// ─── App Entry ────────────────────────────────────────────────────────────────

@Composable
fun MotoStockApp(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determinar el rol activo basado en la ruta actual
    val isClienteFlow = currentRoute in CLIENT_ROUTES
    val isWorkerFlow  = currentRoute in WORKER_ROUTES

    val showBottomNav = isClienteFlow || isWorkerFlow

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                if (isClienteFlow) {
                    MotoStockBottomBar(
                        navController = navController,
                        currentRoute = currentRoute,
                        items = CLIENT_TABS
                    )
                } else {
                    MotoStockBottomBar(
                        navController = navController,
                        currentRoute = currentRoute,
                        items = WORKER_TABS
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(padding)
        ) {
            composable("login") {
                LoginScreen(navController)
            }
            composable("register") {
                RegisterScreen(navController)
            }

            // ── Dashboard Trabajador / Admin ──
            composable("home") {
                HomeScreen(navController)
            }

            // ── Dashboard Cliente ──
            composable("home_cliente") {
                HomeClienteScreen(navController)
            }

            // ── Inventario ──
            composable("inventario") {
                InventarioScreen(navController)
            }
            composable("inventario/form?repuestoId={id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                FormRepuestoScreen(navController, id)
            }
            composable("inventario/form") {
                FormRepuestoScreen(navController)
            }

            // ── Citas Trabajador ──
            composable("citas_trabajador") {
                CitasTrabajadorScreen(navController)
            }

            // ── Historial ──
            composable("historial") {
                HistorialScreen(navController)
            }
            composable("historial/form_servicio") {
                FormServicioScreen(navController)
            }

            // ── Cliente: Agendar y Mis Servicios ──
            composable("agendar_cita") {
                AgendarCitaScreen(navController)
            }
            composable("mis_servicios") {
                MisServiciosScreen(navController)
            }

            // ── Admin: Crear Trabajador ──
            composable("crear_trabajador") {
                CrearTrabajadorScreen(navController)
            }

            // ── Registro Vehicular y En Taller ──
            composable("registro_vehicular") {
                RegistroVehicularScreen(navController)
            }
            composable("en_taller") {
                EnTallerScreen(navController)
            }
        }
    }
}

// ─── Bottom Bar Reutilizable ──────────────────────────────────────────────────

@Composable
fun MotoStockBottomBar(
    navController: NavHostController,
    currentRoute: String?,
    items: List<BottomNavItem>
) {
    NavigationBar(
        containerColor = MotoStockDs.colors.surfaceContainerLowest,
        contentColor = MotoStockDs.colors.primary,
        tonalElevation = MotoStockDs.elevation.large
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        style = if (isSelected) MotoStockDs.typography.labelSmall else MotoStockDs.typography.bodySmall
                    )
                },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MotoStockDs.colors.onPrimaryFixed,
                    selectedTextColor = MotoStockDs.colors.primary,
                    indicatorColor = MotoStockDs.colors.primaryFixed,
                    unselectedIconColor = MotoStockDs.colors.onSurfaceVariant,
                    unselectedTextColor = MotoStockDs.colors.onSurfaceVariant
                ),
                onClick = {
                    if (currentRoute != item.route) {
                        val homeRoute = if (items === CLIENT_TABS) "home_cliente" else "home"
                        navController.navigate(item.route) {
                            popUpTo(homeRoute) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
