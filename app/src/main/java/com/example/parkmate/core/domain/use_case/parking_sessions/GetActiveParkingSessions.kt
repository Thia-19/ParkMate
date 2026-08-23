package com.example.parkmate.core.domain.use_case.parking_sessions

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveParkingSessions @Inject constructor(private val repository: ParkingSessionRepository){
    operator fun invoke(): Flow<List<ParkingSession>> {
        return repository.getActiveParkingSessions()
    }
}