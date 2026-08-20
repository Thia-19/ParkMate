package com.example.parkmate.core.domain.repository

import com.example.parkmate.core.domain.models.SavedLocation
import kotlinx.coroutines.flow.Flow

interface SavedLocationRepository {
    fun getAllSavedLocations(): Flow<List<SavedLocation>>
    suspend fun insertSavedLocation(savedLocation: SavedLocation)
    suspend fun deleteSavedLocation(savedLocation: SavedLocation)
}