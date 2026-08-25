package com.example.parkmate.feature_history.domain.models

import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MonthlySessions(
    val monthLabel: String,
    val totalSessions: Int
)

fun List<ParkingSessionWithVehicleInfo>.aggregateByMonthSessions(): List<MonthlySessions> {
    if (this.isEmpty()) return emptyList()

    val monthYearKeyFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val monthLabelFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    val grouped = this.groupBy { item ->
        monthYearKeyFormat.format(Date(item.session.startTime))
    }

    val minTimestamp = this.minOf { it.session.startTime }
    val maxTimestamp = this.maxOf { it.session.startTime }

    val calendar = Calendar.getInstance().apply {
        timeInMillis = minTimestamp
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val maxCalendar = Calendar.getInstance().apply {
        timeInMillis = maxTimestamp
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val result = mutableListOf<MonthlySessions>()

    while (!calendar.after(maxCalendar)) {
        val key = monthYearKeyFormat.format(calendar.time)
        val count = grouped[key]?.size ?: 0

        result.add(
            MonthlySessions(
                monthLabel = monthLabelFormat.format(calendar.time),
                totalSessions = count
            )
        )

        calendar.add(Calendar.MONTH, 1)
    }
    return result
}