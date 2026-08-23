package com.example.parkmate.core.domain.utils.map

import com.example.parkmate.core.domain.models.Position
import kotlin.math.*

// Calcola la distanza in metri tra due posizioni usando la "formula di Haversine"
fun Position.distanceTo(other: Position): Double {
    val earthRadius = 6371000.0
    val dLat = Math.toRadians(other.latitude - this.latitude)
    val dLon = Math.toRadians(other.longitude - this.longitude)

    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(this.latitude)) * cos(Math.toRadians(other.latitude)) *
            sin(dLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}