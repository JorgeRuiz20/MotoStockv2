package com.taller.motostock.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.taller.motostock.feature.admin.ui.CrearTrabajadorScreen
import com.taller.motostock.feature.auth.ui.LoginScreen
import com.taller.motostock.feature.auth.ui.RegisterScreen
import com.taller.motostock.feature.appointments.ui.CitasTrabajadorScreen
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

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Inicio", Icons.Default.Home)
    object Inventario : BottomNavItem("inventario", "Inventario", Icons.Default.ShoppingCart)
    object Historial : BottomNavItem("historial", "Historial", Icons.Default.List)
    object Citas : BottomNavItem("citas_trabajador", "Citas", Icons.Default.DateRange)
}

@Composable
fun MotoStockApp(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val workerTabRoutes = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Inventario.route,
        BottomNavItem.Historial.route,
        BottomNavItem.Citas.route,
        "registro_vehicular"
    )
    val showBottomNav = currentRoute in workerTabRoutes

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                MotoStockBottomBar(navController, currentRoute)
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
            composable("home") {
                HomeScreen(navController)
            }
            composable("home_cliente") {
                HomeClienteScreen(navController)
            }
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
            composable("citas_trabajador") {
                CitasTrabajadorScreen(navController)
            }
            composable("historial") {
                HistorialScreen(navController)
            }
            composable("historial/form_servicio") {
                FormServicioScreen(navController)
            }
            composable("agendar_cita") {
                AgendarCitaScreen(navController)
            }
            composable("mis_servicios") {
                MisServiciosScreen(navController)
            }
            composable("crear_trabajador") {
                CrearTrabajadorScreen(navController)
            }
            composable("registro_vehicular") {
                RegistroVehicularScreen(navController)
            }
            composable("en_taller") {
                EnTallerScreen(navController)
            }
        }
    }
}

@Composable
fun MotoStockBottomBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Inventario,
        BottomNavItem.Historial,
        BottomNavItem.Citas
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
