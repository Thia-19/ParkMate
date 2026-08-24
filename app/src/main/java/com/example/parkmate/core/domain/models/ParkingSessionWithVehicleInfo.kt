package com.example.parkmate.core.domain.models

import androidx.room.Embedded
import com.example.parkmate.core.domain.models.vehicle.VehicleType

data class ParkingSessionWithVehicleInfo(
    @Embedded val session: ParkingSession,
    val vehicleName: String,
    val vehicleType: VehicleType
)
