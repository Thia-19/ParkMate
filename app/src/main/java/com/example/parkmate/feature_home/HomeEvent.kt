package com.example.parkmate.feature_home

import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType

sealed interface HomeEvent {
    data class AddSession (val session: ParkingSession)
}