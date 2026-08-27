package com.example.parkmate.core.domain.repository

import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.models.parkingEffort.RouteTracking
import kotlinx.coroutines.flow.Flow

interface ParkingEffortRepository {
    suspend fun saveParkingEffort(effort: ParkingEffort)
    fun getAllEfforts(): Flow<List<ParkingEffort>>
    suspend fun getEffortsBySessionId(sessionId: Int): List<ParkingEffort>
    fun getEffortMapPoints(): Flow<List<EffortWithPosition>>

    suspend fun startRouteTracking(tracking: RouteTracking): Boolean
    suspend fun getRouteTracking(sessionId: Int): RouteTracking?
    suspend fun completeRouteTracking(effort: ParkingEffort)
    suspend fun cancelRouteTracking(sessionId: Int)
}