package com.example.parkmate.feature_vehiclesManager.viewModel.util

import com.example.parkmate.core.domain.models.vehicle.VehicleType

enum class VehicleStatusFilter {
    ALL,
    AVAILABLE,
    PARKED
}

data class VehicleFilterState(
    val status: VehicleStatusFilter = VehicleStatusFilter.ALL,
    val type: VehicleType? = null
)