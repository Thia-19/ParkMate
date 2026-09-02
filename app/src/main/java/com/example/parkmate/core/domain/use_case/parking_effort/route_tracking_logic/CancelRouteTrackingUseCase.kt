package com.example.parkmate.core.domain.use_case.parking_effort.route_tracking_logic

import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import javax.inject.Inject

class CancelRouteTrackingUseCase @Inject constructor(
    private val parkingEffortRepository: ParkingEffortRepository
) {
    suspend operator fun invoke(sessionId: Int) {
        if (sessionId <= 0) return

        parkingEffortRepository.cancelRouteTracking(sessionId)
    }
}