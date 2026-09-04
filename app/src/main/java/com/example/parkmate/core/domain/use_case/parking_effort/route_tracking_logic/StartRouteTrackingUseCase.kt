package com.example.parkmate.core.domain.use_case.parking_effort.route_tracking_logic

import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import javax.inject.Inject

class StartRouteTrackingUseCase @Inject constructor(
    private val parkingSessionRepository: ParkingSessionRepository,
    private val parkingEffortRepository: ParkingEffortRepository
) {
    suspend operator fun invoke(sessionId: Int): RouteTrackingStartResult {
        if (sessionId <= 0) return RouteTrackingStartResult.InvalidSessionId

        parkingSessionRepository.getParkingSessionById(sessionId)
            ?: return RouteTrackingStartResult.SessionNotFound

        val existingEffort = parkingEffortRepository.getParkingEffortBySessionId(sessionId)
        if (existingEffort != null) {
            return RouteTrackingStartResult.AlreadyCompleted
        }

        return RouteTrackingStartResult.Started
    }
}

sealed interface RouteTrackingStartResult {
    data object Started : RouteTrackingStartResult
    data object SessionNotFound : RouteTrackingStartResult
    data object AlreadyCompleted : RouteTrackingStartResult
    data object InvalidSessionId : RouteTrackingStartResult
}
