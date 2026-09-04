package com.example.parkmate.core.domain.models.parkingEffort

import androidx.room.Embedded
import com.example.parkmate.core.domain.models.Position

data class EffortWithPosition(
    val effortId: Int = 0,
    @Embedded val position: Position,
    val effortScore: Double = 0.0,
    @Embedded(prefix = "destination_")
    val destinationPosition: Position? = null
)
