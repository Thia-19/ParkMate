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

    /*ADD: At least two different types of visualization must be provided,
such as a geographic heat map showing the distribution of parks over time, or
a chart representing parking costs aggregated by period or category. */

    // State (List or Map)
    val viewMode: HistoryViewMode = HistoryViewMode.LIST

)

enum class HistoryViewMode {
    LIST,
    MAP
}