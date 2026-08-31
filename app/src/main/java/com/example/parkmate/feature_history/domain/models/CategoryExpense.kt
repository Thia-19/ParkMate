package com.example.parkmate.feature_history.domain.models

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.ParkingType


data class CategoryExpense(
    val type: ParkingType,
    val label: String,
    val totalCost: Double,
    val color: Color
)

fun List<ParkingSessionWithVehicleInfo>.aggregateByCategory(context: Context): List<CategoryExpense>{
    val paidSessions = this.filter { it.session.finalCost != 0.0 }
    val total = paidSessions.sumOf { it.session.finalCost ?: 0.0 }

    if (total == 0.0) return emptyList()

    return paidSessions
        .groupBy { it.session.parkingType }
        .mapNotNull { (type, sessions) ->
            val cost = sessions.sumOf { it.session.finalCost ?: 0.0 }
            if (cost <= 0) return@mapNotNull null

            val label = when (type) {
                ParkingType.HOURLY_PAID -> context.getString(R.string.parking_type_hourly)
                ParkingType.FIXED_TICKET -> context.getString(R.string.parking_type_fixed)
                ParkingType.FREE -> context.getString(R.string.parking_type_free)
            }

            val color = when (type) {
                ParkingType.HOURLY_PAID -> Color(0xFF1E88E5)
                ParkingType.FIXED_TICKET -> Color(0xFFFB8C00)
                ParkingType.FREE -> Color(0xFF4CAF50)
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
