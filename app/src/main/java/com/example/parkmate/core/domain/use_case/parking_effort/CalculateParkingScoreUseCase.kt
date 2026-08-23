package com.example.parkmate.core.domain.use_case.parking_effort

import javax.inject.Inject
import kotlin.math.max

class CalculateParkingScoreUseCase @Inject constructor() {

    operator fun invoke(distanceMeters: Double, durationSeconds: Long): Double {
        val durationMinutes = durationSeconds / 60.0

        val distancePenalty = distanceMeters * DISTANCE_PENALTY_PER_METER
        val timePenalty = durationMinutes * TIME_PENALTY_PER_MINUTE
        val penalty = distancePenalty + timePenalty

        return max(MIN_SCORE, MAX_SCORE - penalty)
    }

    companion object {
        private const val MAX_SCORE = 100.0
        private const val MIN_SCORE = 0.0
        private const val DISTANCE_PENALTY_PER_METER = 0.05
        private const val TIME_PENALTY_PER_MINUTE = 1.5
    }
}