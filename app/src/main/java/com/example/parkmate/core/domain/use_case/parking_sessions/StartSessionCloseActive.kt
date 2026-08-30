package com.example.parkmate.core.domain.use_case.parking_sessions

import com.example.parkmate.BackgroundTaskManager
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import javax.inject.Inject

class StartSessionCloseActive @Inject constructor(
    private val repository: ParkingSessionRepository,
    private val backgroundTaskManager: BackgroundTaskManager
) {
    suspend operator fun invoke(
        newSession: ParkingSession,
        isNotificationEnabled: Boolean,
        isNotificationExpiryEnabled: Boolean
    ) {
        val insertedId = repository.startNewSessionCloseActive(newSession)
        val savedSession = newSession.copy(id = insertedId.toInt())

        if (isNotificationEnabled) {
            backgroundTaskManager.startOngoingParkingReminder(savedSession.id)
        }

        if (isNotificationExpiryEnabled && savedSession.parkingType == ParkingType.FIXED_TICKET && savedSession.expiryTime != null) {
            backgroundTaskManager.scheduleParkingExpiryAlerts(
                session = savedSession,
                warningMinutes = 10
            )
        }
    }
}