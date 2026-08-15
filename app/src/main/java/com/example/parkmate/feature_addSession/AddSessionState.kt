package com.example.parkmate.feature_addSession

import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.Vehicle

data class AddSessionState(
    val vehicles: List<Vehicle> = emptyList(),
    val vehicleId: Int? = null,
    val parkingType: ParkingType = ParkingType.FREE,
    val position: Position? = null,

    // HOURLY PAID
    val hourlyRate: Double? = null,

    // FIXED TICKET
    val fixedCost: Double? = null,
    val expiryTime: Long? = null,

    val note: String = "",
    val photoUri: String? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)