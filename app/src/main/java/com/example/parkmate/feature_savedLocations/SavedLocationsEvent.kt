package com.example.parkmate.feature_savedLocations

import com.example.parkmate.core.domain.models.MarkerIconType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.SavedLocation

sealed interface SavedLocationsEvent {
    data class AddSavedLocation(
        val name: String,
        val icon: MarkerIconType,
        val position: Position
    ) : SavedLocationsEvent
    data class DeleteSavedLocation(val location: SavedLocation) : SavedLocationsEvent
    data object OpenCreateModal : SavedLocationsEvent
    data object DismissCreateModal: SavedLocationsEvent
}