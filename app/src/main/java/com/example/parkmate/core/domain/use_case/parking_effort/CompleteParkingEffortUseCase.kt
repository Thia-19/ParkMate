package com.example.parkmate.core.domain.use_case.parking_effort

import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import javax.inject.Inject

class CompleteParkingEffortUseCase @Inject constructor(
    private val repository: ParkingEffortRepository,
    private val calculateScoreUseCase: CalculateParkingScoreUseCase
) {

    /**
     * Calcola il punteggio basato su dati reali e salva l'effort.
     */
    suspend operator fun invoke(
        sessionId: Int,
        distanceMeters: Double,
        durationSeconds: Long
    ) {
        if (sessionId == -1) return

        val score = calculateScoreUseCase(distanceMeters, durationSeconds)

        val effort = ParkingEffort(
            sessionId = sessionId,
            distanceMeters = distanceMeters,
            durationSeconds = durationSeconds,
            effortScore = score
        )

        repository.saveParkingEffort(effort)
    }
}