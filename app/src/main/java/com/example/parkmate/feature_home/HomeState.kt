package com.example.parkmate.feature_home

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.SavedLocation

data class HomeState(
    val sessions: List<ParkingSession> = emptyList(),
    val savedLocations: List <SavedLocation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)