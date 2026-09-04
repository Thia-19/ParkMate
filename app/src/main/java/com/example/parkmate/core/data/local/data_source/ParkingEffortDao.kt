package com.example.parkmate.core.data.local.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingEffortDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingEffort(effort: ParkingEffort)
    @Query("SELECT * FROM parking_effort")
    fun observeAll(): Flow<List<ParkingEffort>>
    @Query("SELECT * FROM parking_effort WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getBySessionId(sessionId: Int): ParkingEffort?
    @Query("DELETE FROM parking_effort WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: Int)
    @Query(
        """
        SELECT 
            e.sessionId AS effortId,
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