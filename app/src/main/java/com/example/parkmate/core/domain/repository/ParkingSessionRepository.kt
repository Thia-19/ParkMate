package com.example.parkmate.core.domain.repository

import com.example.parkmate.core.domain.models.ParkingSession
import kotlinx.coroutines.flow.Flow

interface ParkingSessionRepository {
    fun getActiveParkingSessions(): Flow<List<ParkingSession>>

    fun getAllParkingSessions(): Flow<List<ParkingSession>>

    suspend fun getParkingSessionById(id: Int): ParkingSession?

    suspend fun insertParkingSession(parkingSession: ParkingSession)

    suspend fun deleteParkingSession(parkingSession: ParkingSession)

    suspend fun closeActiveSessionForVehicle(vehicleId: Int, endTime: Long, finalCost: Double)

}