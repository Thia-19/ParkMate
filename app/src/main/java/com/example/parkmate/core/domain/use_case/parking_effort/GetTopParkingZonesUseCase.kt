package com.example.parkmate.core.domain.use_case.parking_effort

import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.parkingEffort.RankedParkingZone
import com.example.parkmate.core.domain.util.map.AppGeocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTopParkingZonesUseCase @Inject constructor(
    private val geocoder: AppGeocoder
) {
    suspend operator fun invoke(
        zones: List<ParkingZone>,
        limit: Int = DEFAULT_LIMIT
    ): List<RankedParkingZone> {
        if (zones.isEmpty()) return emptyList()

        val safeLimit = limit.coerceAtLeast(1)
        val topZones = zones.sortedByDescending { it.averageScore }.take(safeLimit)

        return withContext(Dispatchers.IO) {
            topZones.mapIndexed { index, zone ->
                async {
                    val address = runCatching {
                        geocoder(
                            longitude = zone.centerPosition.longitude,
                            latitude = zone.centerPosition.latitude
                        )
                    }.getOrNull() ?: ""

                    RankedParkingZone(
                        rank = index + 1,
                        zoneId = zone.id,
                        address = address,
                        score = zone.averageScore
                    )
                }
            }.awaitAll()
        }
    }

    private companion object {
        const val DEFAULT_LIMIT = 5
    }
}

