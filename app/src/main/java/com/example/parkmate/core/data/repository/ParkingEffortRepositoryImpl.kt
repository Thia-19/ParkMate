package com.example.parkmate.core.data.repository

import com.example.parkmate.core.data.local.data_source.ParkingEffortDao
import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.models.parkingEffort.RouteTracking
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParkingEffortRepositoryImpl @Inject constructor(
    private val dao: ParkingEffortDao
) : ParkingEffortRepository {

    override suspend fun saveParkingEffort(effort: ParkingEffort) = dao.insertParkingEffort(effort)

    override fun getAllEfforts(): Flow<List<ParkingEffort>> = dao.observeAll()

    override suspend fun getEffortsBySessionId(sessionId: Int): List<ParkingEffort> =
        dao.getBySessionId(sessionId)

    override fun getEffortMapPoints(): Flow<List<EffortWithPosition>> = dao.observeMapPoints()

    override suspend fun startRouteTracking(tracking: RouteTracking): Boolean =
        dao.insertRouteTracking(tracking) != -1L

    override suspend fun getRouteTracking(sessionId: Int): RouteTracking? =
        dao.getRouteTracking(sessionId)

    override suspend fun completeRouteTracking(effort: ParkingEffort) {
        dao.completeRouteTracking(effort)
    }

    override suspend fun cancelRouteTracking(sessionId: Int) {
        dao.deleteRouteTracking(sessionId)
    }
}