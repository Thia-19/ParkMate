package com.example.parkmate.feature_addSession

import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.SavedLocation
import com.example.parkmate.core.domain.models.vehicle.Vehicle

data class AddSessionState(
    val vehicles: List<Vehicle> = emptyList(),
    val locations: List<SavedLocation> = emptyList(),
    val locationId: Int? = null,
    val vehicleId: Int? = null,
    val parkingType: ParkingType = ParkingType.FREE,
    val position: Position? = null,

    val hourlyRate: Double? = null,
    
    val fixedCost: Double? = null,
    val expiryTime: Long? = null,

    val note: String = "",
    val photoUri: String? = null,

    val isNotificationEnabled: Boolean = false,
    val isNotificationExpiryEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,

    // Necessario per tenere traccia del parking effort relativo
    val savedSessionId: Int? = null
)