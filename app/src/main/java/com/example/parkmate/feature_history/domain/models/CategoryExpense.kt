package com.example.parkmate.feature_history.domain.models

import androidx.compose.ui.graphics.Color
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.ParkingType


data class CategoryExpense(
    val type: ParkingType,
    val label: String,
    val totalCost: Double,
    val color: Color
)

fun List<ParkingSessionWithVehicleInfo>.aggregateByCategory(): List<CategoryExpense>{
    val paidSessions = this.filter { !it.session.isActive && it.session.finalCost != null && it.session.finalCost > 0 }
    val total = paidSessions.sumOf { it.session.finalCost ?: 0.0 }

    if (total == 0.0) return emptyList()

    return paidSessions
        .groupBy { it.session.parkingType }
        .mapNotNull { (type, sessions) ->
            val cost = sessions.sumOf { it.session.finalCost ?: 0.0 }
            if (cost <= 0) return@mapNotNull null

            val (label, color) = when (type) {
                ParkingType.HOURLY_PAID -> "Hourly Paid" to Color(0xFF1E88E5)
                ParkingType.FIXED_TICKET -> "Fixed Ticket" to Color(0xFFFB8C00)
                ParkingType.FREE -> "Free" to Color(0xFF4CAF50)
            }

            CategoryExpense(
                type = type,
                label = label,
                totalCost = cost,
                color = color
            )
        }
        .sortedByDescending { it.totalCost }
}
