package com.example.parkmate.core.domain.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.vehicle.Vehicle

data class Position (
    val latitude: Double,
    val longitude: Double
)

enum class ParkingType {
    FREE, HOURLY_PAID, FIXED_TICKET
}

val ParkingType.labelRes: Int
    get() = when (this) {
        ParkingType.HOURLY_PAID -> R.string.parking_type_hourly
        ParkingType.FIXED_TICKET -> R.string.parking_type_fixed
        ParkingType.FREE -> R.string.parking_type_free
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

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
){

}

fun ParkingSession.calculateFinalCost(endTime: Long = System.currentTimeMillis()): Double {
    return when (this.parkingType) {
        ParkingType.FREE -> 0.0
        ParkingType.FIXED_TICKET -> this.fixedCost ?: 0.0
        ParkingType.HOURLY_PAID -> {
            val rate = this.hourlyRate ?: 0.0
            val durationInMillis = (endTime - this.startTime).coerceAtLeast(0L)
            val hours = durationInMillis.toDouble() / (1000 * 60 * 60)
            hours * rate
        }
    }
}