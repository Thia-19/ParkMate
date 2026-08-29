package com.example.parkmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parkmate.feature_addSession.AddSessionScreen
import com.example.parkmate.feature_history.presentation.HistoryScreen
import com.example.parkmate.feature_home.HomeScreen
import com.example.parkmate.feature_parkingEffort.screens.ParkingEffortMapScreen
import com.example.parkmate.feature_settings.SettingsScreen
import com.example.parkmate.feature_vehiclesManager.screens.DisplayVehiclesScreen
import com.example.parkmate.feature_savedLocations.SavedLocationsEvent
import com.example.parkmate.feature_savedLocations.SavedLocationsScreen
import com.example.parkmate.feature_savedLocations.SavedLocationsViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    pendingNavigationAction: String? = null,
    onNavigationHandled: () -> Unit = {}
) {
    LaunchedEffect(pendingNavigationAction) {
        when (pendingNavigationAction) {
            "new_parking" -> {
                navController.navigate(AddSessionRoute) {
                    launchSingleTop = true
                }
                onNavigationHandled()
            }
            "home_parkings" -> {
                navController.navigate(HomeRoute) {
                    popUpTo(HomeRoute) { inclusive = true }
                    launchSingleTop = true
                }
                onNavigationHandled()
            }
        }
    }

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
                },
                onNavigateToSavedLocations = {
                    navController.navigate(SavedLocationsRoute)
                }
            )
        }

        composable<AddSessionRoute> {
            AddSessionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<SavedLocationsRoute> {
            val viewModel: SavedLocationsViewModel = hiltViewModel()

            SavedLocationsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEvent = { event ->
                    viewModel.onEvent(event)
                }
            )
        }

        composable<VehicleListRoute> {
            DisplayVehiclesScreen()
        }
        composable<ParkingEffortRoute> {
            ParkingEffortMapScreen()
        }
        composable<HistoryRoute> {
            HistoryScreen()
        }
    }
}