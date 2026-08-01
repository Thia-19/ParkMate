package com.example.parkmate.core.domain.use_case.parking_sessions

import com.example.parkmate.core.domain.repository.ParkingSessionRepository

class CloseActiveParkingSession (private val repository: ParkingSessionRepository){
    suspend operator fun invoke(vehicleId: Int){
        val currentTime = System.currentTimeMillis()
        repository.closeActiveSessionForVehicle(vehicleId, currentTime)
    }
}