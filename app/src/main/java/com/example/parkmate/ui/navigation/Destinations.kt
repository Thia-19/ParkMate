package com.example.parkmate.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface TopLevelRoute

@Serializable
object HomeRoute : TopLevelRoute

@Serializable
object VehicleListRoute : TopLevelRoute

@Serializable
object ParkingEffortRoute : TopLevelRoute

@Serializable
object HistoryRoute : TopLevelRoute

/*
// Se una rotta ha dei parametri (es. il dettaglio di un prodotto), usiamo una data class:
@Serializable
data class DetailRoute(val itemId: String)

*/

data class BottomBarItem(
    val label: String,
    val icon: ImageVector,
    val route: TopLevelRoute
)

val bottomBarItems = listOf(
    BottomBarItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = HomeRoute
    ),
    BottomBarItem(
        label = "Vehicles",
        icon = Icons.Default.DirectionsCar,
        route = VehicleListRoute
    ),
    BottomBarItem(
        label = "Effort",
        icon = Icons.Default.Calculate,
        route = ParkingEffortRoute
    ),
    BottomBarItem(
        label = "History",
        icon = Icons.Default.History,
        route = HistoryRoute
    )
)

@Serializable
object AddSessionRoute