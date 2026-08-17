package com.example.parkmate.core.data.local.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parkmate.core.domain.models.ParkingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingSessionDao {
    @Query("SELECT * FROM parkingsession WHERE isActive = 1")
    fun getActiveParkingSessions(): Flow<List<ParkingSession>>

    @Query("SELECT * FROM parkingsession WHERE isActive = 0 AND endTime IS NOT NULL")
    fun getAllParkingSessions(): Flow<List<ParkingSession>>

    @Query("SELECT * FROM parkingsession WHERE id = :id")
    suspend fun getParkingSessionById(id: Int): ParkingSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingSession(parkingSession: ParkingSession)

    @Delete
    suspend fun deleteParkingSession(parkingSession: ParkingSession)

    @Query("UPDATE parkingsession SET isActive = 0, endTime = :endTime, finalCost = :finalCost WHERE vehicleId = :vehicleId AND isActive = 1")
    suspend fun closeActiveParkingSession(vehicleId: Int, endTime: Long, finalCost: Double)
}

