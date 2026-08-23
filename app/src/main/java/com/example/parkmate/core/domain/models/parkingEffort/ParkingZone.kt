package com.example.parkmate.core.domain.models.parkingEffort
import com.example.parkmate.core.domain.models.Position

data class ParkingZone(
    val centerPosition: Position,
    val averageScore: Double,
    val parkingCount: Int,
    val colorHex: String
)