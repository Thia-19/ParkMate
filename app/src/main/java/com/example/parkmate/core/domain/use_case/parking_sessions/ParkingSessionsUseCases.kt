package com.example.parkmate.core.domain.use_case.parking_sessions

data class ParkingSessionsUseCases(
    val getActiveParkingSessions: GetActiveParkingSessions,
    val getParkingSessions: GetAllParkingSessions,
    val deleteParkingSession: DeleteParkingSession,
    val addParkingSession: AddParkingSession,
    val getParkingSession: GetParkingSession,
    val closeActiveParkingSession: CloseActiveParkingSession
)
