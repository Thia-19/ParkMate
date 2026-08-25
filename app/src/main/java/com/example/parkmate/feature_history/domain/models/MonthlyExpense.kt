package com.example.parkmate.feature_history.domain.models

import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MonthlyExpense(
    val monthLabel: String,
    val totalCost: Double
)

fun List<ParkingSessionWithVehicleInfo>.aggregateByMonth(): List<MonthlyExpense> {
    val monthYearFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val monthNameFormat = SimpleDateFormat("MMM", Locale.getDefault())

    return this.filter { it.session.finalCost != null }
        .groupBy { item ->
            val date = Date(item.session.startTime)
            monthYearFormat.format(date)
        }
        .toSortedMap()
        .map { (_, items) ->
            val firstSessionDate = Date(items.first().session.startTime)
            MonthlyExpense(
                monthLabel = monthNameFormat.format(firstSessionDate),
                totalCost = items.sumOf { it.session.finalCost ?: 0.0 }
            )
        }
}
