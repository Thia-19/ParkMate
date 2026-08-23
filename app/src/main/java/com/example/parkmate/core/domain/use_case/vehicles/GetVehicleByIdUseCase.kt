package com.example.parkmate.core.domain.use_case.vehicles

import com.example.parkmate.core.domain.repository.VehiclesRepository
import javax.inject.Inject

class GetVehicleByIdUseCase @Inject constructor(
    private val repository: VehiclesRepository
) {
    operator fun invoke(id: Int) = repository.getVehicleById(id)
}