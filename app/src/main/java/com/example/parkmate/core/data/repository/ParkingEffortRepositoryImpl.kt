package com.example.parkmate.core.data.repository

import com.example.parkmate.core.data.local.data_source.ParkingEffortDao
import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParkingEffortRepositoryImpl @Inject constructor(
    private val dao: ParkingEffortDao
) : ParkingEffortRepository {

    override suspend fun saveParkingEffort(effort: ParkingEffort) {
        dao.insertParkingEffort(effort)
    }

    override fun getAllEfforts(): Flow<List<ParkingEffort>> = dao.observeAll()

    override suspend fun getParkingEffortBySessionId(sessionId: Int): ParkingEffort? =
        dao.getBySessionId(sessionId)

    override fun getEffortMapPoints(): Flow<List<EffortWithPosition>> = dao.observeMapPoints()

    override suspend fun cancelRouteTracking(sessionId: Int) {
        dao.deleteParkingEffortBySessionId(sessionId)
    }
}