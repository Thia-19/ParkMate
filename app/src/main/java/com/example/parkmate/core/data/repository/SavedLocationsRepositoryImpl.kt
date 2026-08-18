package com.example.parkmate.core.data.repository

import com.example.parkmate.core.data.local.data_source.SavedLocationDao
import com.example.parkmate.core.domain.models.SavedLocation
import com.example.parkmate.core.domain.repository.SavedLocationRepository
import kotlinx.coroutines.flow.Flow

class SavedLocationsRepositoryImpl (private val savedLocationDao: SavedLocationDao): SavedLocationRepository {
    override fun getAllSavedLocations(): Flow<List<SavedLocation>> {
        return savedLocationDao.getAllSavedLocations()
    }

    override suspend fun insertSavedLocation(savedLocation: SavedLocation){
        return savedLocationDao.insertSavedLocation(savedLocation)
    }
}