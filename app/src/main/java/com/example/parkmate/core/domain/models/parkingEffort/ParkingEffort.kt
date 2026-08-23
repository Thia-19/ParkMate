package com.example.parkmate.core.domain.models.parkingEffort

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Embedded
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
    ]
)
data class ParkingEffort(
    @PrimaryKey(autoGenerate = true) val effortId: Int = 0,
    val sessionId: Int,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val effortScore: Double
)


data class EffortWithPosition(
    @Embedded val position: Position,
    val effortScore: Double
)

// NOTA: il punteggio va da 0 a 100
