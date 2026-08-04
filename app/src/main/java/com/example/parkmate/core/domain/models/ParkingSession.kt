package com.example.parkmate.core.domain.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Position (
    val latitude: Double,
    val longitude: Double
)

enum class ParkingType {
    FREE, HOURLY_PAID, FIXED_TICKET
}
@Entity
data class ParkingSession(
    val isActive: Boolean,
    val vehicleId: Int,
    val parkingType: ParkingType,
    val startTime: Long,
    @Embedded val position: Position,

    // HOURLY PAID
    val hourlyRate: Double? = null,

    // FIXED TICKET
    val fixedCost: Double? = null,
    val expiryTime: Long? = null,

    val note: String? = null,
    val photoUri: String? = null,

    val endTime: Long? = null,
    val finalCost: Double? = null,

    @PrimaryKey val id: Int
){

}
