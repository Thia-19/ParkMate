package com.example.parkmate.core.data.repository

import com.example.parkmate.core.data.local.data_source.ParkingSessionDao
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import kotlinx.coroutines.flow.Flow

class ParkingSessionsRepositoryImpl (private val parkingSessionDao: ParkingSessionDao): ParkingSessionRepository{
    override fun getActiveParkingSessions(): Flow<List<ParkingSessionWithVehicleInfo>> {
        return parkingSessionDao.getActiveParkingSessionsWithVehicle()
    }

    override fun getConcludedParkingSessions(): Flow<List<ParkingSessionWithVehicleInfo>> {
        return parkingSessionDao.getConcludedParkingSessionsWithVehicle()
    }

    override suspend fun getActiveSessionByVehicle(vehicleId: Int): ParkingSession? {
        return parkingSessionDao.getActiveSessionByVehicle(vehicleId)
    }

    override suspend fun insertParkingSession(parkingSession: ParkingSession) {
        return parkingSessionDao.insertParkingSession(parkingSession)
    }

    override suspend fun deleteParkingSession(parkingSession: ParkingSession) {
        return parkingSessionDao.deleteParkingSession(parkingSession)
    }

    override suspend fun closeActiveSessionForVehicle(sessionId: Int, endTime: Long, finalCost: Double) {
        return parkingSessionDao.closeActiveParkingSession(sessionId, endTime, finalCost)
    }

    override suspend fun startNewSessionCloseActive(newSession: ParkingSession) {
        return parkingSessionDao.startNewSessionCloseActive(newSession)
    }
}