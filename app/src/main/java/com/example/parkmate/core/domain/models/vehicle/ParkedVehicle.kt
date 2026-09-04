package com.example.parkmate.core.domain.models.vehicle

import com.example.parkmate.core.domain.models.ParkingSession

data class ParkedVehicle(
    val vehicle: Vehicle,
    val parkingSession: ParkingSession?
) {
    val isParked: Boolean
        get() = parkingSession != null
}

