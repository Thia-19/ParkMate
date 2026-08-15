package com.example.parkmate.feature_home

import com.example.parkmate.core.domain.models.ParkingSession

data class HomeState(
    val sessions: List<ParkingSession> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)