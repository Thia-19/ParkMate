package com.example.parkmate.core.data.local.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingSessionDao {
    @Query("""
        SELECT 
            p.*, 
            v.name AS vehicleName, 
            v.type AS vehicleType 
        FROM parkingsession p
        INNER JOIN vehicles v ON p.vehicleId = v.id
        WHERE p.isActive = 1
    """)
    fun getActiveParkingSessionsWithVehicle(): Flow<List<ParkingSessionWithVehicleInfo>>

    @Query("""
        SELECT 
            p.*, 
            v.name AS vehicleName, 
            v.type AS vehicleType 
        FROM parkingsession p
        INNER JOIN vehicles v ON p.vehicleId = v.id
        WHERE p.isActive = 0 AND p.endTime IS NOT NULL
    """)
    fun getConcludedParkingSessionsWithVehicle(): Flow<List<ParkingSessionWithVehicleInfo>>

    @Query("SELECT * FROM parkingsession WHERE id = :id")
    suspend fun getParkingSessionById(id: Int): ParkingSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingSession(parkingSession: ParkingSession)

    @Delete
    suspend fun deleteParkingSession(parkingSession: ParkingSession)

    @Query("UPDATE parkingsession SET isActive = 0, endTime = :endTime, finalCost = :finalCost WHERE vehicleId = :vehicleId AND isActive = 1")
    suspend fun closeActiveParkingSession(vehicleId: Int, endTime: Long, finalCost: Double)
}



