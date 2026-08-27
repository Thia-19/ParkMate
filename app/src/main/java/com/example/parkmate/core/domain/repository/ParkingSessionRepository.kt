package com.example.parkmate.core.domain.repository

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import kotlinx.coroutines.flow.Flow

interface ParkingSessionRepository {
    fun getActiveParkingSessions(): Flow<List<ParkingSessionWithVehicleInfo>>

    fun getConcludedParkingSessions(): Flow<List<ParkingSessionWithVehicleInfo>>

    suspend fun getActiveSessionByVehicle(vehicleId: Int): ParkingSession?

    suspend fun getParkingSessionById(id: Int): ParkingSession?

    suspend fun insertParkingSession(parkingSession: ParkingSession)

    suspend fun deleteParkingSession(parkingSession: ParkingSession)

    suspend fun closeActiveSessionForVehicle(sessionId: Int, endTime: Long, finalCost: Double)

    suspend fun startNewSessionCloseActive (newSession: ParkingSession)

}