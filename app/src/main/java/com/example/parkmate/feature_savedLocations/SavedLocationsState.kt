package com.example.parkmate.feature_savedLocations

import com.example.parkmate.core.domain.models.SavedLocation

data class SavedLocationsState (
    val locations: List<SavedLocation> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = "",
    val locationToEdit: SavedLocation? = null,
    val isCreateModalOpen: Boolean = false
)