package com.example.parkmate.core.domain.use_case.parking_effort

import android.location.Location
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import javax.inject.Inject

class GetNearestParkingZoneUseCase @Inject constructor() {

    operator fun invoke(zones: List<ParkingZone>, targetLat: Double, targetLng: Double): ParkingZone? {
        return invoke(zones, Position(latitude = targetLat, longitude = targetLng))
    }

    operator fun invoke(zones: List<ParkingZone>, targetPosition: Position): ParkingZone? {
        if (zones.isEmpty()) return null

        val results = FloatArray(1)
        return zones.minByOrNull { zone ->
            Location.distanceBetween(
                zone.centerPosition.latitude,
                zone.centerPosition.longitude,
                targetPosition.latitude,
                targetPosition.longitude,
                results
            )
            results[0]
        }
    }
}