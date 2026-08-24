package com.example.parkmate.feature_history.presentation

import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.vehicle.Vehicle

data class HistoryState(
    val allSessions: List<ParkingSessionWithVehicleInfo> = emptyList(),
    val sessions: List<ParkingSessionWithVehicleInfo> = emptyList(),
    val userVehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Filters
    val selectedVehicleId: Int? = null,
    val selectedParkingType: ParkingType? = null,
    val selectedStartDate: Long? = null,
    val selectedEndDate: Long? = null,

    // History tab
    val selectedTab: HistoryTab = HistoryTab.LIST
)

enum class HistoryTab {
    LIST,
    ANALYTICS,
    MAP
}