package com.example.parkmate.core.domain.models.parkingEffort

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.Position

@Entity(
    tableName = "parking_effort",
    foreignKeys = [
        ForeignKey(
            entity = ParkingSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ParkingEffort (
    @PrimaryKey val sessionId: Int,
    val distanceMeters: Double,
    val timeFrame: Long,
    val steps: Int = 0,
    val elevationGain: Double = 0.0,
    val effortScore: Double = 0.0,
    @Embedded(prefix = "destination_")
    val destination: Position? = null
)




