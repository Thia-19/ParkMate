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

    fun Long.toMillis(): Long = if (this < 10_000_000_000L) this * 1000L else this

    val monthYearKeyFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val monthLabelFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    val grouped = this.groupBy { item ->
        monthYearKeyFormat.format(Date(item.session.startTime.toMillis()))
    }

    val minTimestamp = this.minOf { it.session.startTime }.toMillis()
    val maxTimestamp = this.maxOf { it.session.startTime }.toMillis()

    val calendar = Calendar.getInstance().apply {
        timeInMillis = minTimestamp
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val maxCalendar = Calendar.getInstance().apply {
        timeInMillis = maxTimestamp
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
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