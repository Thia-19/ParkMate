package com.example.parkmate.core.domain.use_case.vehicles

import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.repository.VehiclesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVehiclesUseCase @Inject constructor(
    private val repository: VehiclesRepository
) {
    operator fun invoke() : Flow<List<Vehicle>> = repository.getVehicles()
}

