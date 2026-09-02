package com.example.parkmate.feature_vehiclesManager.util

import androidx.annotation.StringRes
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.vehicle.VehicleType

@get:StringRes
val VehicleType.labelRes: Int
    get() = when (this) {
        VehicleType.CAR -> R.string.vehicle_type_car
        VehicleType.MOTORCYCLE -> R.string.vehicle_type_motorcycle
        VehicleType.BICYCLE -> R.string.vehicle_type_bicycle
        VehicleType.OTHER -> R.string.vehicle_type_other
    }