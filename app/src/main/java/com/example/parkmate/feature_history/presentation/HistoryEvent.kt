package com.example.parkmate.feature_history.presentation

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.ParkingType

sealed interface HistoryEvent {
    // change mode
    data class OnTabSelected(val tab: HistoryTab) : HistoryEvent

    // filters
    data class FilterByVehicle(val vehicleId: Int?) : HistoryEvent
    data class FilterByParkType(val parkType: ParkingType?) : HistoryEvent
    data class FilterByDateRange(
        val startDate: Long?,
        val endDate: Long?
    ) : HistoryEvent
    object ResetFilters : HistoryEvent

    data class DeleteSession(val session: ParkingSession) : HistoryEvent

    data class OnSessionClick(val item: ParkingSessionWithVehicleInfo) : HistoryEvent
}