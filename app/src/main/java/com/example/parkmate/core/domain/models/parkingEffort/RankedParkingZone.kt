package com.example.parkmate.core.domain.models.parkingEffort

data class RankedParkingZone(
    val rank: Int,
    val zoneId: String,
    val address: String,
    val score: Double
)