package com.example.parkmate.core.domain.use_case.parking_effort

import android.location.Location
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import javax.inject.Inject
import kotlin.math.roundToInt

class GenerateParkingZonesUseCase @Inject constructor() {

    operator fun invoke(efforts: List<EffortWithPosition>): List<ParkingZone> {
        if (efforts.isEmpty()) return emptyList()

        val mutableEfforts = efforts.toMutableList()
        val clusters = mutableListOf<MutableList<EffortWithPosition>>()
        val results = FloatArray(1)

        while (mutableEfforts.isNotEmpty()) {
            val currentEffort = mutableEfforts.removeAt(0)
            val cluster = mutableListOf(currentEffort)

            val iterator = mutableEfforts.iterator()
            while (iterator.hasNext()) {
                val otherEffort = iterator.next()

                Location.distanceBetween(
                    currentEffort.position.latitude,
                    currentEffort.position.longitude,
                    otherEffort.position.latitude,
                    otherEffort.position.longitude,
                    results
                )

                if (results[0] <= CLUSTER_RADIUS_METERS) {
                    cluster.add(otherEffort)
                    iterator.remove()
                }
            }
            clusters.add(cluster)
        }

        return clusters.mapIndexed { index, cluster ->
            val avgLat = cluster.map { it.position.latitude }.average()
            val avgLon = cluster.map { it.position.longitude }.average()

            val rawAvgScore = cluster.map { it.effortScore }.average()
            val roundedAvgScore = (rawAvgScore * 100.0).roundToInt() / 100.0

            val topDestinations = cluster
                .sortedByDescending { it.effortId }
                .mapNotNull { it.destinationPosition }
                .distinct()
                .take(MAX_RECENT_DESTINATIONS)

            ParkingZone(
                id = "zone_$index",
                centerPosition = Position(avgLat, avgLon),
                averageScore = roundedAvgScore,
                parkingCount = cluster.size,
                recentDestinations = topDestinations
            )
        }
    }

    private companion object {
        const val CLUSTER_RADIUS_METERS = 300.0
        const val MAX_RECENT_DESTINATIONS = 10
    }
}

