package com.example.parkmate.feature_home

import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.SavedLocation

data class HomeState(
    val sessions: List<ParkingSessionWithVehicleInfo> = emptyList(),
    val savedLocations: List <SavedLocation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)