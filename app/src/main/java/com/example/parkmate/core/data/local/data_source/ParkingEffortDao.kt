package com.example.parkmate.core.data.local.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import kotlinx.coroutines.flow.Flow
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort

// Operations : Create, GetBySessionId, GetAll
@Dao
interface ParkingEffortDao {

    @Insert
    suspend fun insertParkingEffort(effort: ParkingEffort)


    @Query("SELECT * FROM parking_effort")
    fun getAllEfforts(): Flow<List<ParkingEffort>>

    @Query("SELECT * FROM parking_effort WHERE sessionId = :sessionId")
    suspend fun getEffortsBySessionId(sessionId: Int): List<ParkingEffort>

    @Query("""
        SELECT s.latitude, s.longitude, e.effortScore 
        FROM parking_effort e 
        INNER JOIN ParkingSession s ON e.sessionId = s.id
    """)
    fun getEffortMapPoints(): Flow<List<EffortWithPosition>>

}
