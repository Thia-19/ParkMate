package com.example.parkmate.feature_history.domain.models

import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.vehicle.Vehicle

data class ParkingMetrics (
    val avgCostPerSession: Double = 0.0,
    val avgDurationMinutes: Long = 0,
    val mostUsedVehicle: String? = null,
    val preferredParkingType: ParkingType? = null
)

fun List<ParkingSessionWithVehicleInfo>.calculateMetrics(vehicles: List<Vehicle>): ParkingMetrics {
    if (this.isEmpty()) return ParkingMetrics()

    val totalCost = this.sumOf { it.session.finalCost ?: 0.0 }
    val avgCost = totalCost / this.size

    val totalDurationMs = this.sumOf { item ->
        (item.session.endTime!! - item.session.startTime).coerceAtLeast(0L)
    }
    val avgDurationMs = totalDurationMs / this.size
    val avgDurationMin = avgDurationMs / (1000 * 60)

    val mostUsedVehicleId = this.groupBy { it.session.vehicleId }
        .maxByOrNull { it.value.size }?.key

    val mostUsedVehicle = vehicles.find { it.id == mostUsedVehicleId }?.name
        ?: this.firstOrNull { it.session.vehicleId == mostUsedVehicleId }?.vehicleName
        ?: "N/D"

    val preferredType: ParkingType? = this.groupBy { it.session.parkingType }
        .maxByOrNull { it.value.size }?.key

    return ParkingMetrics(
        avgCostPerSession = avgCost,
        avgDurationMinutes = avgDurationMin,
        mostUsedVehicle = mostUsedVehicle,
        preferredParkingType = preferredType
    )
}