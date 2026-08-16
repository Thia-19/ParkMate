package com.example.parkmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parkmate.feature_addSession.AddSessionScreen
import com.example.parkmate.feature_history.presentation.HistoryScreen
import com.example.parkmate.feature_home.HomeScreen
import com.example.parkmate.feature_settings.SettingsScreen


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
            HomeScreen(
                onNavigateToAddSession = {
                    navController.navigate(AddSessionRoute)
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsRoute)
                }
            )
        }

        composable<AddSessionRoute> {
            AddSessionScreen (
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<SettingsRoute>{
            SettingsScreen (
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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