package com.example.parkmate.feature_history.domain.models

import com.example.parkmate.core.domain.models.ParkingSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MonthlyExpense(
    val monthLabel: String,
    val totalCost: Double
)

fun List<ParkingSession>.aggregateByMonth(): List<MonthlyExpense> {
    val monthYearFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val monthNameFormat = SimpleDateFormat("MMM", Locale.getDefault())

    return this.filter { !it.isActive && it.finalCost != null && it.endTime != null }
        .groupBy { session ->
            val date = Date(session.startTime)
            monthYearFormat.format(date) // Identificatore unico per ordinare: "2026-08"
        }
        .toSortedMap()
        .map { (_, sessions) ->
            val firstSessionDate = Date(sessions.first().startTime)
            MonthlyExpense(
                monthLabel = monthNameFormat.format(firstSessionDate),
                totalCost = sessions.sumOf { it.finalCost ?: 0.0 }
            )
        }
}
