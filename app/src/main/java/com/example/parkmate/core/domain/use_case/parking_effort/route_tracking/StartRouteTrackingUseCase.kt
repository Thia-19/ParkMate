package com.example.parkmate.core.domain.use_case.parking_effort.route_tracking

import com.example.parkmate.core.domain.models.parkingEffort.RouteTracking
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import javax.inject.Inject

class StartRouteTrackingUseCase @Inject constructor(
    private val parkingSessionRepository: ParkingSessionRepository,
    private val parkingEffortRepository: ParkingEffortRepository
) {
    suspend operator fun invoke(
        sessionId: Int,
        startedAtMillis: Long = System.currentTimeMillis()
    ): RouteTrackingStartResult {
        if (sessionId <= 0) return RouteTrackingStartResult.InvalidSessionId

        val session = parkingSessionRepository.getParkingSessionById(sessionId)
            ?: return RouteTrackingStartResult.SessionNotFound

        //if (session.isActive) return RouteTrackingStartResult.SessionStillActive :: RACE CONDITION

        if (parkingEffortRepository.getEffortsBySessionId(sessionId).isNotEmpty()) {
            return RouteTrackingStartResult.AlreadyCompleted
        }

        val inserted = parkingEffortRepository.startRouteTracking(
            RouteTracking(sessionId = sessionId, startedAtMillis = startedAtMillis)
        )

        return if (inserted) {
            RouteTrackingStartResult.Started
        } else {
            RouteTrackingStartResult.AlreadyStarted
        }
    }
}

sealed interface RouteTrackingStartResult {
    data object Started : RouteTrackingStartResult
    data object AlreadyStarted : RouteTrackingStartResult
    data object SessionNotFound : RouteTrackingStartResult
    data object SessionStillActive : RouteTrackingStartResult
    data object AlreadyCompleted : RouteTrackingStartResult
    data object InvalidSessionId : RouteTrackingStartResult
}