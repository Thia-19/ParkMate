package com.example.parkmate.core.domain.use_case.parking_effort.route_tracking

import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.core.domain.use_case.parking_effort.CalculateParkingScoreUseCase
import com.example.parkmate.core.domain.util.map.distanceTo
import javax.inject.Inject

class CompleteRouteTrackingUseCase @Inject constructor(
    private val parkingSessionRepository: ParkingSessionRepository,
    private val parkingEffortRepository: ParkingEffortRepository,
    private val calculateParkingScore: CalculateParkingScoreUseCase
) {
    suspend operator fun invoke(
        sessionId: Int,
        destination: Position,
        completedAtMillis: Long = System.currentTimeMillis()
    ): RouteTrackingCompletionResult {
        if (sessionId <= 0) return RouteTrackingCompletionResult.InvalidSessionId

        val tracking = parkingEffortRepository.getRouteTracking(sessionId)
            ?: return RouteTrackingCompletionResult.NoActiveTracking

        val session = parkingSessionRepository.getParkingSessionById(sessionId)
            ?: return RouteTrackingCompletionResult.SessionNotFound

        val durationSeconds = ((completedAtMillis - tracking.startedAtMillis) / 1_000)
            .coerceAtLeast(0L)
        val distanceMeters = session.position.distanceTo(destination)
        val score = calculateParkingScore(distanceMeters, durationSeconds)

        val effort = ParkingEffort(
            sessionId = sessionId,
            distanceMeters = distanceMeters,
            durationSeconds = durationSeconds,
            effortScore = score,
            destinationPosition = destination
        )
        parkingEffortRepository.completeRouteTracking(effort)

        return RouteTrackingCompletionResult.Completed(effort)
    }
}

sealed interface RouteTrackingCompletionResult {
    data class Completed(val effort: ParkingEffort) : RouteTrackingCompletionResult
    data object NoActiveTracking : RouteTrackingCompletionResult
    data object SessionNotFound : RouteTrackingCompletionResult
    data object InvalidSessionId : RouteTrackingCompletionResult
}