package com.example.parkmate.core.domain.use_case.vehicles

import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.repository.ImageRepository
import com.example.parkmate.core.domain.repository.VehiclesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveVehicleUseCase @Inject constructor(
    private val vehiclesRepository: VehiclesRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(
        vehicle: Vehicle,
        newImageUriString: String? = null,
        isUpdate: Boolean = false
    ): Unit = withContext(Dispatchers.IO) {

        val oldImagePath = vehicle.imageUri
        val newImagePath: String? =
            if (newImageUriString != null && newImageUriString != oldImagePath) {
                imageRepository.saveImage(newImageUriString)
            } else {
                null
            }

        val finalImagePath = newImagePath ?: oldImagePath
        val vehicleToSave = vehicle.copy(imageUri = finalImagePath)

        try {
            if (isUpdate) {
                vehiclesRepository.updateVehicle(vehicleToSave)
                if (newImagePath != null && oldImagePath != null) {
                    imageRepository.deleteImage(oldImagePath)
                }
            } else {
                vehiclesRepository.addVehicle(vehicleToSave)
            }
        } catch (e: Exception) {
            newImagePath?.let { path -> imageRepository.deleteImage(path) }
            throw e
        }
    }
}