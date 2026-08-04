package com.example.parkmate.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parkmate.ui.components.AppBottomBar
import com.example.parkmate.ui.navigation.AppNavHost
import com.example.parkmate.ui.navigation.HistoryRoute
import com.example.parkmate.ui.navigation.HomeRoute
import com.example.parkmate.ui.navigation.ParkingEffortRoute
import com.example.parkmate.ui.navigation.TopLevelRoute
import com.example.parkmate.ui.navigation.VehicleListRoute

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentRoute: TopLevelRoute? = when {
        currentDestination?.hasRoute<HomeRoute>() == true -> HomeRoute
        currentDestination?.hasRoute<VehicleListRoute>() == true -> VehicleListRoute
        currentDestination?.hasRoute<ParkingEffortRoute>() == true -> ParkingEffortRoute
        currentDestination?.hasRoute<HistoryRoute>() == true -> HistoryRoute
        else -> null
    }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                currentRoute = currentRoute,
                onNavigateToRoute = { targetRoute ->
                    navController.navigate(targetRoute) {
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}