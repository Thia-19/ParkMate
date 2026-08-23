package com.example.parkmate.feature_addSession

import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position

sealed interface AddSessionEvent {
    // Form options
    data class OnVehicleSelected(val vehicleId: Int) : AddSessionEvent
    data class OnLocationSelected(val locationId: Int?) : AddSessionEvent
    data class OnNoteChanged(val note: String) : AddSessionEvent
    data class OnParkingTypeChanged (val type: ParkingType) : AddSessionEvent
    data class OnHourlyRateChanged (val rate: Double?) : AddSessionEvent
    data class OnFixedCostChanged (val cost: Double?) : AddSessionEvent
    data class OnExpiryTimeChanged (val time: Long?) : AddSessionEvent
    data class OnPhotoCaptured(val uri: String) : AddSessionEvent

    // Save form
    data object OnSaveSession : AddSessionEvent

    // Position handling
    data class OnPositionFetched(val position: Position) : AddSessionEvent
    data object FetchLocationRequested : AddSessionEvent
}