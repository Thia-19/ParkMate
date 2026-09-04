package com.example.parkmate.core.domain.use_case.parking_effort.route_tracking_logic

import android.database.sqlite.SQLiteConstraintException
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

        val safeTimeFrame = timeFrame.coerceAtLeast(0L)
        val safeDistanceMeters = realDistanceMeters.coerceAtLeast(0.0)
        val safeSteps = totalSteps.coerceAtLeast(0)
        val safeElevationGain = elevationGain.coerceAtLeast(0.0)

        if (destination == null && safeDistanceMeters == 0.0 && safeSteps == 0 && safeElevationGain == 0.0) {
            repository.cancelRouteTracking(sessionId)
            return RouteTrackingCompletionResult.NoActiveTracking
        }

        if (repository.getParkingEffortBySessionId(sessionId) != null) {
            return RouteTrackingCompletionResult.AlreadyCompleted
        }

        val draftEffort = ParkingEffort(
            sessionId = sessionId,
            distanceMeters = safeDistanceMeters,
            timeFrame = safeTimeFrame,
            steps = safeSteps,
            elevationGain = safeElevationGain,
            effortScore = 0.0,
            destination = destination
        )

        val effortScore = calculateParkingScoreUseCase(draftEffort)
        val parkingEffort = draftEffort.copy(effortScore = effortScore)

        return try {
            repository.saveParkingEffort(parkingEffort)
            RouteTrackingCompletionResult.Completed(parkingEffort)
        } catch (e: SQLiteConstraintException) {
            RouteTrackingCompletionResult.AlreadyCompleted
        } catch (e: Exception) {
            RouteTrackingCompletionResult.Failed(e.message ?: "Errore sconosciuto durante il salvataggio")
        }
    }
}

sealed interface RouteTrackingCompletionResult {
    data class Completed(val effort: ParkingEffort) : RouteTrackingCompletionResult
    data class Failed(val reason: String) : RouteTrackingCompletionResult
    data object NoActiveTracking : RouteTrackingCompletionResult
    data object InvalidSessionId : RouteTrackingCompletionResult
    data object AlreadyCompleted : RouteTrackingCompletionResult
}

