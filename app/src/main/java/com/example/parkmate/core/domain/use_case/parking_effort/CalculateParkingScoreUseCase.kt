package com.example.parkmate.core.domain.use_case.parking_effort

import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateParkingScoreUseCase @Inject constructor() {

    companion object {
        private const val WEIGHT_DISTANCE = 0.40
        private const val WEIGHT_TIME = 0.20
        private const val WEIGHT_STEPS = 0.15
        private const val WEIGHT_ELEVATION = 0.05

        private const val MAX_REF_DISTANCE_METERS = 1500.0 // 1.5 km
        private const val MAX_REF_TIME_SECONDS = 1200.0    // 20 minuti
        private const val MAX_REF_STEPS = 2000.0           // 2000 passi
        private const val MAX_REF_ELEVATION_METERS = 50.0  // 50 metri
    }

    operator fun invoke(effort: ParkingEffort): Double {
        var weightedEffortSum = 0.0
        var totalActiveWeight = 0.0

        if (effort.timeFrame > 0) {
            val normalizedTime = (effort.timeFrame / MAX_REF_TIME_SECONDS).coerceAtMost(1.0)
            weightedEffortSum += normalizedTime * WEIGHT_TIME
            totalActiveWeight += WEIGHT_TIME
        }

        if (effort.distanceMeters > 0.0) {
            val normalizedDistance = (effort.distanceMeters / MAX_REF_DISTANCE_METERS).coerceAtMost(1.0)
            weightedEffortSum += normalizedDistance * WEIGHT_DISTANCE
            totalActiveWeight += WEIGHT_DISTANCE
        }

        if (effort.steps > 0) {
            val normalizedSteps = (effort.steps / MAX_REF_STEPS).coerceAtMost(1.0)
            weightedEffortSum += normalizedSteps * WEIGHT_STEPS
            totalActiveWeight += WEIGHT_STEPS
        }

        if (effort.elevationGain > 0.0) {
            val normalizedElevation = (effort.elevationGain / MAX_REF_ELEVATION_METERS).coerceAtMost(1.0)
            weightedEffortSum += normalizedElevation * WEIGHT_ELEVATION
            totalActiveWeight += WEIGHT_ELEVATION
        }

        if (totalActiveWeight == 0.0) return 0.0

        val effortRatio = weightedEffortSum / totalActiveWeight
        val convenienceScore = (1.0 - effortRatio) * 100.0

        return (convenienceScore * 100.0).roundToInt() / 100.0
    }
}