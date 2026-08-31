package com.example.parkmate.feature_history.domain.models

import java.util.Calendar
import androidx.annotation.StringRes
import com.example.parkmate.R


enum class TimeRange(@StringRes val labelResId: Int) {
    LAST_MONTH(R.string.history_last_month),
    LAST_SIX_MONTHS(R.string.history_last_6),
    ALL_TIME(R.string.history_all_time);

    fun getStartTimestamp(): Long? {
        if (this == ALL_TIME) return null

        val calendar = Calendar.getInstance()
        when (this) {
            LAST_MONTH -> calendar.add(Calendar.MONTH, -1)
            LAST_SIX_MONTHS -> calendar.add(Calendar.MONTH, -6)
            ALL_TIME -> {}
        }
        return calendar.timeInMillis
    }
}