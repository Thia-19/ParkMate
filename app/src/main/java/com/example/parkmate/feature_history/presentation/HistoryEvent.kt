package com.example.parkmate.feature_history.presentation

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType

// Eventi (cambio filtro, toggle mappa/lista, click su sessione)
sealed interface HistoryEvent {
    // change mode
    data object ChangeViewMode : HistoryEvent
    // data class OnSessionClick(val session: ParkingSession) : HistoryEvent

    // filters
    data class FilterByVehicle(val vehicleId: Int?) : HistoryEvent
    data class FilterByParkType(val parkType: ParkingType?) : HistoryEvent
    data class FilterByDateRange(
        val startDate: Long?,
        val endDate: Long?
    ) : HistoryEvent
    object ResetFilters : HistoryEvent

    // Azione opzionale sulla singola sessione
    data class DeleteSession(val session: ParkingSession) : HistoryEvent

    data class OnSessionClick(val session: ParkingSession) : HistoryEvent

}