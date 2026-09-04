package com.example.parkmate.core.data.repository

import com.example.parkmate.core.data.local.data_source.VehicleDao
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.repository.VehiclesRepository
import javax.inject.Inject

class VehiclesRepositoryImpl @Inject constructor(
    private val dao: VehicleDao
) : VehiclesRepository {

    override suspend fun addVehicle(vehicle: Vehicle) : Long = dao.insertVehicle(vehicle)

    override fun getVehicles() = dao.getVehicles()

    override fun getVehicleById(id: Int) = dao.getVehicleById(id)

    override suspend fun updateVehicle(vehicle: Vehicle) = dao.updateVehicle(vehicle)

    override suspend fun deleteVehicle(vehicle: Vehicle) = dao.deleteVehicle(vehicle)
}

