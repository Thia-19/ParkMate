package com.example.parkmate.core.domain.repository

import com.example.parkmate.core.domain.models.vehicle.Vehicle
import kotlinx.coroutines.flow.Flow

// Astrazione del database

interface VehiclesRepository {

    suspend fun addVehicle(vehicle: Vehicle)

    fun getVehicles(): Flow<List<Vehicle>>

    fun getVehicleById(id: Int): Flow<Vehicle?>

    suspend fun updateVehicle(vehicle: Vehicle)

    suspend fun deleteVehicle(vehicle: Vehicle)
}