package com.example.parkmate.feature_history.domain.models

import java.util.Calendar

enum class TimeRange (val label: String){
    LAST_MONTH("Last Month"),
    LAST_SIX_MONTHS("Last 6 Months"),
    ALL_TIME ("All time");

    fun getStartTimestamp(): Long? {
        if (this == ALL_TIME) return null

        val calendar = Calendar.getInstance()
        when(this){
            LAST_MONTH -> calendar.add(Calendar.MONTH, -1)
            LAST_SIX_MONTHS -> calendar.add(Calendar.MONTH, -6)
            ALL_TIME -> {}
        }
        return calendar.timeInMillis
    }
}