package com.example.parkmate.feature_home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.use_case.parking_sessions.ParkingSessionsUseCases
import com.example.parkmate.core.domain.use_case.saved_locations.SavedLocationUseCases
import com.example.parkmate.core.preferences.domain.use_case.SettingsUseCases
import com.example.parkmate.permissions.RouteTrackingPermissions
import com.example.parkmate.service.activity_recognition.VehicleToPedestrianActivityRecognitionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val useCases: ParkingSessionsUseCases,
    private val savedLocationsUseCases: SavedLocationUseCases,
    private val settingsUseCases: SettingsUseCases,
    private val vehicleActivityRecognitionManager: VehicleToPedestrianActivityRecognitionManager
) : ViewModel() {

    init {
        observeActivityRecognitionSetting()
    }

    val state: StateFlow<HomeState> = combine(
        useCases.getActiveParkingSessions(),
        savedLocationsUseCases.getAllSavedLocations()
    ) { sessions, savedLocations ->
        HomeState(
            sessions = sessions,
            savedLocations = savedLocations,
            isLoading = false,
            errorMessage = null
        )
    }
        .catch { e ->
            emit(
                HomeState(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Unknown error"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeState(isLoading = true)
        )

    private fun observeActivityRecognitionSetting() {
        viewModelScope.launch {
            settingsUseCases.getActivityRecognitionHome().collect { isEnabled ->
                val hasPermission = RouteTrackingPermissions.isActivityRecognitionEnabled(context)

                if (isEnabled && hasPermission) {
                    try {
                        vehicleActivityRecognitionManager.startMonitoring()
                    } catch (e: SecurityException) {
                        Log.e("HomeViewModel", "No permissions x startMonitoring", e)
                    }
                } else {
                    if (hasPermission) {
                        try {
                            vehicleActivityRecognitionManager.stopMonitoring()
                        } catch (e: SecurityException) {
                            Log.e("HomeViewModel", "No x permissions startMonitoring", e)
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.AddSession -> {
                viewModelScope.launch {
                    useCases.addParkingSession(event.session)
                }
            }
            is HomeEvent.CloseActiveSession -> {
                viewModelScope.launch {
                    useCases.closeActiveParkingSession(event.session)
                }
            }
            is HomeEvent.DeleteSession -> {
                viewModelScope.launch {
                    useCases.deleteParkingSession(event.session)
                }
            }
            is HomeEvent.OnSessionClick -> {
                viewModelScope.launch {

                }
            }
        }
    }
}


