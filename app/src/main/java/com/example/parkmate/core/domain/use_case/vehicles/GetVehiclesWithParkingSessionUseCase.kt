package com.example.parkmate.core.domain.use_case.vehicles

import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.core.domain.repository.VehiclesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetVehiclesWithParkingSessionUseCase @Inject constructor(
    private val vehiclesRepository: VehiclesRepository,
    private val parkingSessionRepository: ParkingSessionRepository
) {
    operator fun invoke(): Flow<List<ParkedVehicle>> {
        return combine(
            vehiclesRepository.getVehicles(),
            parkingSessionRepository.getActiveParkingSessions()
        )
        { vehicles, activeSessions ->
            val sessionsByVehicleId = activeSessions.associateBy { it.vehicleId }

            vehicles.map { vehicle ->
                ParkedVehicle(
                    vehicle = vehicle,
                    parkingSession = sessionsByVehicleId[vehicle.id]
                )
            }
        }
    }
}