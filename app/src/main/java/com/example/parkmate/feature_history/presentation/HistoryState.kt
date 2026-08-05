package com.example.parkmate.feature_history.presentation

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Vehicle


// will survive screen rotations
// Mantiene i dati filtrati, la vista attiva (LISTA/MAPPA), ecc.
data class HistoryState(
    val allSession: List<ParkingSession> = emptyList(),
    val sessions: List<ParkingSession> = emptyList(),
    val userVehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // ordine: val parkingSessionOrder: ParkingSessionOrder = Park...

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
