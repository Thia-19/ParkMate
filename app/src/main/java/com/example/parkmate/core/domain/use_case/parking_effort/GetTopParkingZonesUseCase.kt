package com.example.parkmate.core.domain.use_case.parking_effort

import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.parkingEffort.RankedParkingZone
import com.example.parkmate.core.domain.util.AppGeocoder
import javax.inject.Inject

class GetTopParkingZonesUseCase @Inject constructor(
    private val geocoder: AppGeocoder
) {
    suspend operator fun invoke(
        zones: List<ParkingZone>,
        limit: Int = DEFAULT_LIMIT
    ): List<RankedParkingZone> {
        val safeLimit = limit.coerceAtLeast(1)

        return zones
            .sortedByDescending { it.averageScore }
            .take(safeLimit)
            .mapIndexed { index, zone ->

                val address = geocoder(
                    longitude = zone.centerPosition.longitude,
                    latitude = zone.centerPosition.latitude
                )

                RankedParkingZone(
                    rank = index + 1,
                    zoneId = zone.id,
                    address = address,
                    score = zone.averageScore
                )
            }
    }

    private companion object {
        const val DEFAULT_LIMIT = 5
    }
}