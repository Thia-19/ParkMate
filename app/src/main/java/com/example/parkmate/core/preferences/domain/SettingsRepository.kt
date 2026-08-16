package com.example.parkmate.core.preferences.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getUseCurrentPosition(): Flow<Boolean>
    suspend fun setUseCurrentPosition(enabled: Boolean)
}