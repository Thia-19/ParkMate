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
data class ParkingEffort(
    @PrimaryKey(autoGenerate = true) val effortId: Int = 0,
    val sessionId: Int,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val effortScore: Double,
    @Embedded(prefix = "destination_")
    val destinationPosition: Position? = null
)

data class EffortWithPosition(
    val effortId: Int = 0,
    @Embedded val position: Position,
    val effortScore: Double,
    @Embedded(prefix = "destination_")
    val destinationPosition: Position? = null
)
