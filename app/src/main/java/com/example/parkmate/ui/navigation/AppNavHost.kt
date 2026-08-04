package com.example.parkmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parkmate.feature_history.presentation.HistoryScreen
import com.example.parkmate.feature_home.HomeScreen


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen()
        }
        composable<VehicleListRoute>{
            // zara, qui devi mettere il tuo ...screen.kt
        }
        composable<ParkingEffortRoute>{
            // zara, qui devi mettere il tuo ...screen.kt
        }
        composable<HistoryRoute>{
            HistoryScreen()
        }
    }
}