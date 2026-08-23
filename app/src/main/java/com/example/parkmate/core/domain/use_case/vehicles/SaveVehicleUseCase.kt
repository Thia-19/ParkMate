package com.example.parkmate.core.domain.use_case.vehicles

import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.repository.ImageRepository
import com.example.parkmate.core.domain.repository.VehiclesRepository
import javax.inject.Inject

class SaveVehicleUseCase @Inject constructor(
    private val vehiclesRepository: VehiclesRepository,
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(
        vehicle: Vehicle,
        newImageUriString: String? = null,
        isUpdate: Boolean = false
    ) {
        val oldImagePath = vehicle.imageUri
        var newImagePath: String? = null


        if (newImageUriString != null && newImageUriString != oldImagePath) {
            newImagePath = imageRepository.saveImage(newImageUriString)
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

            // Rollback: se il DB fallisce, eliminiamo la nuova foto appena creata
            newImagePath?.let { path ->
                imageRepository.deleteImage(path)
            }
            throw e
        }
    }
}