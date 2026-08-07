package com.example.parkmate.feature_history.domain.models

import androidx.compose.ui.graphics.Color
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.Vehicle

data class VehicleExpense(
    val vehicle: Vehicle,
    val totalCost: Double,
    val color: Color
)

// i colori secondo me andranno poi messi nel res
val VehiclePalette = listOf(
    Color(0xFF4285F4),
    Color(0xFFEA4335),
    Color(0xFFFBBC05),
    Color(0xFF34A853),
    Color(0xFF8E24AA),
    Color(0xFFF26D21)
)

fun List<ParkingSession>.aggregateByVehicle(
    vehiclesList: List<Vehicle>
): List<VehicleExpense> {
    val vehicleMap = vehiclesList.associateBy { it.id }

    return this.filter { !it.isActive && it.finalCost != null }
        .groupBy { it.vehicleId }
        .entries
        .mapIndexedNotNull { index, entry ->
            val vehicleId = entry.key

            val vehicle = vehicleMap[vehicleId] ?: return@mapIndexedNotNull null
            val sessions = entry.value
            val total = sessions.sumOf { it.finalCost ?: 0.0}

            VehicleExpense(
                vehicle = vehicle,
                totalCost = total,
                color = VehiclePalette[index % VehiclePalette.size]
            )
        }
        .sortedByDescending { it.totalCost }
}