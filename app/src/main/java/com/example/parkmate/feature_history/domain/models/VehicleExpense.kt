package com.example.parkmate.feature_history.domain.models

import androidx.compose.ui.graphics.Color
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.vehicle.Vehicle

data class VehicleExpense(
    val vehicle: Vehicle,
    val totalCost: Double,
    val color: Color
)

val VehiclePalette = listOf(
    Color(0xFF4285F4),
    Color(0xFFEA4335),
    Color(0xFFFBBC05),
    Color(0xFF34A853),
    Color(0xFF8E24AA),
    Color(0xFFF26D21)
)

fun List<ParkingSessionWithVehicleInfo>.aggregateByVehicle(
    vehiclesList: List<Vehicle>
): List<VehicleExpense> {
    val vehicleMap = vehiclesList.associateBy { it.id }

    return this.filter { it.session.finalCost != 0.0 }
        .groupBy { it.session.vehicleId }
        .entries
        .mapIndexedNotNull { index, entry ->
            val vehicleId = entry.key

            val vehicle = vehicleMap[vehicleId] ?: return@mapIndexedNotNull null
            val sessions = entry.value
            val total = sessions.sumOf { it.session.finalCost ?: 0.0}

            VehicleExpense(
                vehicle = vehicle,
                totalCost = total,
                color = VehiclePalette[index % VehiclePalette.size]
            )
        }
        .sortedByDescending { it.totalCost }
}