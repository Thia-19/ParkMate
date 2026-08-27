package com.example.parkmate.core.domain.util.map

import com.example.parkmate.core.domain.models.Position
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun Position.distanceTo(other: Position): Double {
    val latitudeDelta = Math.toRadians(other.latitude - latitude)
    val longitudeDelta = Math.toRadians(other.longitude - longitude)
    val latitude = Math.toRadians(latitude)
    val otherLatitude = Math.toRadians(other.latitude)

    val haversine = (sin(latitudeDelta / 2) * sin(latitudeDelta / 2) +
            cos(latitude) * cos(otherLatitude) *
            sin(longitudeDelta / 2) * sin(longitudeDelta / 2)).coerceIn(0.0, 1.0)

    return EARTH_RADIUS_METERS * 2 * atan2(sqrt(haversine), sqrt(1 - haversine))
}

private const val EARTH_RADIUS_METERS = 6_371_000.0