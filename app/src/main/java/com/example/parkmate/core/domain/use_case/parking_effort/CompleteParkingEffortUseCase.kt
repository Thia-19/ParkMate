package com.example.parkmate.core.domain.use_case.parking_effort

import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import javax.inject.Inject

class CompleteParkingEffortUseCase @Inject constructor(
    private val repository: ParkingEffortRepository,
    private val calculateScore: CalculateParkingScoreUseCase
) {

    suspend operator fun invoke(
        sessionId: Int,
        distanceMeters: Double,
        durationSeconds: Long,
        destinationPosition: Position? = null
    ): Result<ParkingEffort> {
        if (sessionId <= 0) {
            return Result.failure(IllegalArgumentException("A valid parking session ID is required."))
        }

        val safeDistance = distanceMeters.coerceAtLeast(0.0)
        val safeDuration = durationSeconds.coerceAtLeast(0L)

        val effort = ParkingEffort(
            sessionId = sessionId,
            distanceMeters = safeDistance,
            durationSeconds = safeDuration,
            effortScore = calculateScore(safeDistance, safeDuration),
            destinationPosition = destinationPosition
        )

        repository.saveParkingEffort(effort)
        return Result.success(effort)
    }
}