package com.example.parkmate.core.domain.use_case.parking_sessions

import javax.inject.Inject

data class ParkingSessionsUseCases @Inject constructor(
    val getActiveParkingSessions: GetActiveParkingSessions,
    val getConcludedParkingSessions: GetConcludedParkingSessions,
    val deleteParkingSession: DeleteParkingSession,
    val addParkingSession: AddParkingSession,
    val getParkingSession: GetParkingSession,
    val closeActiveParkingSession: CloseActiveParkingSession
)
