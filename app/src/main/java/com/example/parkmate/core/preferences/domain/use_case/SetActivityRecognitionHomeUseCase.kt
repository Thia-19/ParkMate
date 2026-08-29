package com.example.parkmate.core.preferences.domain.use_case

import com.example.parkmate.core.preferences.domain.SettingsRepository
import javax.inject.Inject

class SetActivityRecognitionHomeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setActivityRecognitionHome(enabled)
    }
}