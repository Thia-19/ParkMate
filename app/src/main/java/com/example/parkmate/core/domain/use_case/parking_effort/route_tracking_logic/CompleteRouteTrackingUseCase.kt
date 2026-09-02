package com.example.parkmate.core.domain.use_case.parking_effort.route_tracking_logic

import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.use_case.parking_effort.CalculateParkingScoreUseCase
import javax.inject.Inject

class CompleteRouteTrackingUseCase @Inject constructor(
    private val repository: ParkingEffortRepository,
    private val calculateParkingScoreUseCase: CalculateParkingScoreUseCase
) {
    suspend operator fun invoke(
        sessionId: Int,
        timeFrame: Long,
        destination: Position?,
        realDistanceMeters: Double,
        totalSteps: Int,
        elevationGain: Double
    ): RouteTrackingCompletionResult {
        if (sessionId <= 0) {
            return RouteTrackingCompletionResult.InvalidSessionId
        }

        // Gestione "Null Trap": Tracciamento troppo breve o nullo
        if (destination == null && realDistanceMeters == 0.0 && totalSteps == 0) {
            repository.cancelRouteTracking(sessionId)
            return RouteTrackingCompletionResult.NoActiveTracking
        }

        // Costruzione provvisoria (serve come input per il calcolo dello score,
        // che lavora su un oggetto ParkingEffort)
        val draftEffort = ParkingEffort(
            sessionId = sessionId,
            distanceMeters = realDistanceMeters,
            timeFrame = timeFrame,
            steps = totalSteps,
            elevationGain = elevationGain,
            effortScore = 0.0,
            destination = destination
        )

        val effortScore = calculateParkingScoreUseCase(draftEffort)
        val parkingEffort = draftEffort.copy(effortScore = effortScore)

        repository.saveParkingEffort(parkingEffort)
        return RouteTrackingCompletionResult.Completed(parkingEffort)
    }
}

sealed interface RouteTrackingCompletionResult {
    data class Completed(val effort: ParkingEffort) : RouteTrackingCompletionResult
    data object NoActiveTracking : RouteTrackingCompletionResult
    data object InvalidSessionId : RouteTrackingCompletionResult
}