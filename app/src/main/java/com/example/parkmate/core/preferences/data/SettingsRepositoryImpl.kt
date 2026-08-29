package com.example.parkmate.core.preferences.data

import com.example.parkmate.core.preferences.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {
    override fun getUseCurrentPosition(): Flow<Boolean> {
        return dataStore.useCurrentPositionFlow
    }

    override suspend fun setUseCurrentPosition(enabled: Boolean) {
        dataStore.setUseCurrentPosition(enabled)
    }

    override fun getActivityRecognitionHome(): Flow<Boolean> {
        return dataStore.activityRecognitionHomeFlow
    }

    override suspend fun setActivityRecognitionHome(enabled: Boolean) {
        dataStore.setActivityRecognitionHome(enabled)
    }
}