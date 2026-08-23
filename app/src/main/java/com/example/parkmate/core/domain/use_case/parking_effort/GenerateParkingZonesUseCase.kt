package com.example.parkmate.core.domain.use_case.parking_effort

import com.example.parkmate.core.domain.models.parkingEffort.EffortWithPosition
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.utils.map.distanceTo
import com.example.parkmate.core.domain.utils.map.getConvenienceColorHex
import javax.inject.Inject

class GenerateParkingZonesUseCase @Inject constructor() {

    operator fun invoke(
        efforts: List<EffortWithPosition>,
        mergeRadiusMeters: Double = DEFAULT_MERGE_RADIUS_METERS
    ): List<ParkingZone> {

        // 1. Creazione dei Cluster basati sulla distanza
        val clusters = mutableListOf<MutableList<EffortWithPosition>>()

        for (effort in efforts) {
            // Trova il primo cluster in cui la distanza tra il centro del cluster e la sessione è <= al raggio
            val targetCluster = clusters.firstOrNull { cluster ->
                val clusterCenterPos = cluster.first().position
                effort.position.distanceTo(clusterCenterPos) <= mergeRadiusMeters
            }

            if (targetCluster != null) {
                targetCluster.add(effort)
            } else {
                clusters.add(mutableListOf(effort))
            }
        }

        // 2. Mappatura dei cluster in ParkingZone (Heatmap)
        return clusters.map { cluster ->
            val avgLat = cluster.map { it.position.latitude }.average()
            val avgLng = cluster.map { it.position.longitude }.average()
            val clusterAvgScore = cluster.map { it.effortScore }.average()

            ParkingZone(
                centerPosition = Position(avgLat, avgLng),
                averageScore = clusterAvgScore,
                parkingCount = cluster.size,
                colorHex = getConvenienceColorHex(clusterAvgScore)
            )
        }
    }

    companion object {
        private const val DEFAULT_MERGE_RADIUS_METERS = 150.0
    }
}