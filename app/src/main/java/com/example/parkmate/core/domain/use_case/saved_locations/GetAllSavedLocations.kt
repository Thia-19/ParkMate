package com.example.parkmate.core.domain.use_case.saved_locations

import com.example.parkmate.core.domain.models.SavedLocation
import com.example.parkmate.core.domain.repository.SavedLocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSavedLocations @Inject constructor(private val repository: SavedLocationRepository){
    operator fun invoke(): Flow<List<SavedLocation>> {
        return repository.getAllSavedLocations()
    }
}