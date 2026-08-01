package com.example.parkmate.core.domain.use_case.parking_sessions

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.repository.ParkingSessionRepository

class AddParkingSession (private val repository: ParkingSessionRepository){
    suspend operator fun invoke(parkingSession: ParkingSession){
        // TODO: Eccezione per invalid parking Session
        repository.insertParkingSession(parkingSession)
    }
}