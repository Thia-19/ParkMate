package com.example.parkmate.core.domain.use_case.parking_effort.route_tracking

import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import javax.inject.Inject

class CancelRouteTrackingUseCase @Inject constructor(
    private val parkingEffortRepository: ParkingEffortRepository
) {
    suspend operator fun invoke(sessionId: Int): CancelRouteTrackingResult {
        if (sessionId <= 0) return CancelRouteTrackingResult.InvalidSessionId

        parkingEffortRepository.cancelRouteTracking(sessionId)
        return CancelRouteTrackingResult.Cancelled
    }
}

sealed interface CancelRouteTrackingResult {
    data object Cancelled : CancelRouteTrackingResult
    data object InvalidSessionId : CancelRouteTrackingResult
}