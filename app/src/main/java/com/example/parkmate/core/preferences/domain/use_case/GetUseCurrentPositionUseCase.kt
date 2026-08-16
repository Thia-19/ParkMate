package com.example.parkmate.core.preferences.domain.use_case

import com.example.parkmate.core.preferences.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUseCurrentPositionUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.getUseCurrentPosition()
}