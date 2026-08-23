package com.example.parkmate.core.domain.use_case.saved_locations

import com.example.parkmate.core.domain.models.SavedLocation
import com.example.parkmate.core.domain.repository.SavedLocationRepository
import javax.inject.Inject

class DeleteSavedLocation @Inject constructor(private val repository: SavedLocationRepository){
    suspend operator fun invoke(savedLocation: SavedLocation){
        repository.deleteSavedLocation(savedLocation)
    }
}