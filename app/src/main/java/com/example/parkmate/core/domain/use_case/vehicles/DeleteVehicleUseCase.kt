package com.example.parkmate.core.domain.use_case.vehicles

import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.repository.ImageRepository
import com.example.parkmate.core.domain.repository.VehiclesRepository
import javax.inject.Inject

class DeleteVehicleUseCase @Inject constructor(
    private val vehiclesRepository: VehiclesRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(vehicle: Vehicle) {
        try {

            vehiclesRepository.deleteVehicle(vehicle)

            vehicle.imageUri?.let { path ->
                imageRepository.deleteImage(path)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}