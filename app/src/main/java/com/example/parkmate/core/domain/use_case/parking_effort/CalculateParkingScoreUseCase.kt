package com.example.parkmate.core.domain.use_case.parking_effort

import javax.inject.Inject

class CalculateParkingScoreUseCase @Inject constructor() {

    operator fun invoke(distanceMeters: Double, durationSeconds: Long): Double {
        val safeDistanceMeters = distanceMeters.coerceAtLeast(0.0)
        val safeDurationSeconds = durationSeconds.coerceAtLeast(0L)
        val durationMinutes = safeDurationSeconds / SECONDS_PER_MINUTE

        val penalty =
            safeDistanceMeters * DISTANCE_PENALTY_PER_METER +
                    durationMinutes * TIME_PENALTY_PER_MINUTE

        return (MAX_SCORE - penalty).coerceIn(MIN_SCORE, MAX_SCORE)
    }

    private companion object {
        const val MIN_SCORE = 0.0
        const val MAX_SCORE = 100.0
        const val SECONDS_PER_MINUTE = 60.0
        const val DISTANCE_PENALTY_PER_METER = 0.05
        const val TIME_PENALTY_PER_MINUTE = 1.5
    }
}