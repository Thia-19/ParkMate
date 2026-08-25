package com.example.parkmate.core.domain.use_case.parking_sessions

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.calculateFinalCost
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import javax.inject.Inject

class CloseActiveParkingSession @Inject constructor(
    private val repository: ParkingSessionRepository
) {
    suspend operator fun invoke(session: ParkingSession) {
        val currentTime = System.currentTimeMillis()
        val calculatedCost = session.calculateFinalCost(currentTime)

        repository.closeActiveSessionForVehicle(
            sessionId = session.id,
            endTime = currentTime,
            finalCost = calculatedCost
        )
    }
}