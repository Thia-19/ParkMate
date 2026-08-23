package com.example.parkmate.core.domain.use_case.parking_sessions

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import javax.inject.Inject

class AddParkingSession @Inject constructor(private val repository: ParkingSessionRepository){
    suspend operator fun invoke(parkingSession: ParkingSession){
        // TODO: Eccezione per invalid parking Session
        repository.insertParkingSession(parkingSession)
    }
}