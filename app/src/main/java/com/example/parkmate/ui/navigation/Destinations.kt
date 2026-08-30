package com.example.parkmate.ui.navigation


import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.parkmate.R
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

data class BottomBarItem(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val route: TopLevelRoute
)

val bottomBarItems = listOf(
    BottomBarItem(
        labelRes = R.string.feature_home,
        icon = Icons.Default.Home,
        route = HomeRoute
    ),
    BottomBarItem(
        labelRes = R.string.feature_vehicles,
        icon = Icons.Default.DirectionsCar,
        route = VehicleListRoute
    ),
    BottomBarItem(
        labelRes = R.string.feature_effort,
        icon = Icons.Default.Calculate,
        route = ParkingEffortRoute
    ),
    BottomBarItem(
        labelRes = R.string.feature_history,
        icon = Icons.Default.History,
        route = HistoryRoute
    )
)

@Serializable
object AddSessionRoute

@Serializable
object SettingsRoute

@Serializable
object SavedLocationsRoute