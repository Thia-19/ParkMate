package com.example.parkmate.feature_home

import com.example.parkmate.core.domain.models.ParkingSession

sealed interface HomeEvent {
    data class AddSession (val session: ParkingSession) : HomeEvent
    data class CloseActiveSession (val session: ParkingSession) : HomeEvent
    data class DeleteSession (val session: ParkingSession) : HomeEvent

    data class OnSessionClick (val session: ParkingSession) : HomeEvent

}