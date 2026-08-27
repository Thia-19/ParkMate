package com.example.parkmate.core.data.local.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.models.parkingEffort.RouteTracking
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingEffortDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertParkingEffort(effort: ParkingEffort)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRouteTracking(tracking: RouteTracking): Long

    @Query("SELECT * FROM parking_effort")
    fun observeAll(): Flow<List<ParkingEffort>>

    @Query("SELECT * FROM parking_effort WHERE sessionId = :sessionId")
    suspend fun getBySessionId(sessionId: Int): List<ParkingEffort>

    @Query("SELECT * FROM route_tracking WHERE sessionId = :sessionId")
    suspend fun getRouteTracking(sessionId: Int): RouteTracking?

    @Query("DELETE FROM route_tracking WHERE sessionId = :sessionId")
    suspend fun deleteRouteTracking(sessionId: Int)

    @Transaction
    suspend fun completeRouteTracking(effort: ParkingEffort) {
        insertParkingEffort(effort)
        deleteRouteTracking(effort.sessionId)
    }

    @Query(
        """
        SELECT 
            e.effortId AS effortId,
            s.latitude AS latitude,
            s.longitude AS longitude,
            e.effortScore AS effortScore,
            e.destination_latitude AS destination_latitude,
            e.destination_longitude AS destination_longitude
        FROM parking_effort AS e
        INNER JOIN ParkingSession AS s ON s.id = e.sessionId
        """
    )
    fun observeMapPoints(): Flow<List<EffortWithPosition>>
}