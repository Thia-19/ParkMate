package com.example.parkmate.core.domain.use_case.parking_effort

import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.util.map.distanceTo
import javax.inject.Inject

class GetNearestParkingZoneUseCase @Inject constructor() {

    operator fun invoke(zones: List<ParkingZone>, targetLat: Double, targetLng: Double): ParkingZone? {
        return invoke(zones, Position(latitude = targetLat, longitude = targetLng))
    }

    operator fun invoke(zones: List<ParkingZone>, targetPosition: Position): ParkingZone? {
        if (zones.isEmpty()) return null
        return zones.minByOrNull { zone -> zone.centerPosition.distanceTo(targetPosition) }
    }
}