package com.example.parkmate.core.domain.models.vehicle

import com.example.parkmate.core.domain.models.ParkingSession


// Oggetto assembleto e temporaneo da passare alla UI.
data class ParkedVehicle(
    val vehicle: Vehicle,
    val parkingSession: ParkingSession?
) {
    // custom getter
    val isParked: Boolean
        get() = parkingSession != null
}

