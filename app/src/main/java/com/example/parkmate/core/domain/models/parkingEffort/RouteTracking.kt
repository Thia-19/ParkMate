package com.example.parkmate.core.domain.models.parkingEffort

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.parkmate.core.domain.models.ParkingSession

@Entity(
    tableName = "route_tracking",
    foreignKeys = [
        ForeignKey(
            entity = ParkingSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RouteTracking(
    @PrimaryKey val sessionId: Int,
    val startedAtMillis: Long
)