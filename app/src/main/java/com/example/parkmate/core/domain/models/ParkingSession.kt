package com.example.parkmate.core.domain.models

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.vehicle.Vehicle

data class Position (
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null
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
    fun calculateFinalCost(endTime: Long): Double {
        return when (this.parkingType) {
            ParkingType.FIXED_TICKET -> this.fixedCost ?: 0.0
            ParkingType.HOURLY_PAID -> {
                val rate = this.hourlyRate ?: 0.0
                val durationMillis = endTime - this.startTime
                val durationHours = durationMillis / (1000.0 * 60 * 60)
                durationHours * rate
            }
            ParkingType.FREE -> 0.0
        }
    }

    fun getOngoingReminderMessage(
        context: Context,
        currentTime: Long = System.currentTimeMillis()
    ): Pair<String, String> {
        val durationMs = currentTime - startTime
        val hours = durationMs / (1000 * 60 * 60)
        val minutes = (durationMs / (1000 * 60)) % 60

        val elapsedTimeString = when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }

        val title = "${context.getString(R.string.notifications_title_alert)} ($elapsedTimeString)"

        val content = when (parkingType) {
            ParkingType.HOURLY_PAID -> {
                val estimatedCost = calculateFinalCost(currentTime)
                val costString = String.format("%.2f €", estimatedCost)
                "${context.getString(R.string.home_elapsed_time)}: $elapsedTimeString • ${context.getString(R.string.home_accumulated_cost)}: $costString"
            }
            ParkingType.FIXED_TICKET -> {
                val remainingMs = (expiryTime ?: currentTime) - currentTime
                val remMinutes = maxOf(0L, remainingMs / (1000 * 60))
                "${context.getString(R.string.home_elapsed_time)}: $elapsedTimeString • ${context.getString(R.string.notifications_remaining_time)}: ${remMinutes}m"
            }
            ParkingType.FREE -> {
                "${context.getString(R.string.home_elapsed_time)}: $elapsedTimeString ${context.getString(R.string.home_free_parking)}"
            }
        }

        return Pair(title, content)
    }
}