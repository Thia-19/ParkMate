package com.example.parkmate.core.domain.use_case.parking_effort

import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.util.map.distanceTo
import javax.inject.Inject

class GenerateParkingZonesUseCase @Inject constructor() {

    operator fun invoke(efforts: List<EffortWithPosition>): List<ParkingZone> {
        if (efforts.isEmpty()) return emptyList()

        val mutableEfforts = efforts.toMutableList()
        val clusters = mutableListOf<MutableList<EffortWithPosition>>()

        while (mutableEfforts.isNotEmpty()) {
            val currentEffort = mutableEfforts.removeAt(0)
            val cluster = mutableListOf(currentEffort)

            val iterator = mutableEfforts.iterator()
            while (iterator.hasNext()) {
                val otherEffort = iterator.next()
                val distance = currentEffort.position.distanceTo(otherEffort.position)

                if (distance <= CLUSTER_RADIUS_METERS) {
                    cluster.add(otherEffort)
                    iterator.remove()
                }
            }
            clusters.add(cluster)
        }

        return clusters.mapIndexed { index, cluster ->
            val avgLat = cluster.map { it.position.latitude }.average()
            val avgLon = cluster.map { it.position.longitude }.average()
            val avgScore = cluster.map { it.effortScore }.average()

            val top10Destinations = cluster
                .sortedByDescending { it.effortId }
                .mapNotNull { it.destinationPosition }
                .distinct()
                .take(MAX_RECENT_DESTINATIONS)

            ParkingZone(
                id = "zone_$index",
                centerPosition = Position(avgLat, avgLon),
                averageScore = avgScore,
                parkingCount = cluster.size,
                recentDestinations = top10Destinations
            )
        }
    }

    private companion object {
        const val CLUSTER_RADIUS_METERS = 300.0
        const val MAX_RECENT_DESTINATIONS = 10
    }
}