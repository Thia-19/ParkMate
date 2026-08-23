package com.example.parkmate.feature_savedLocations

import com.example.parkmate.core.domain.models.MarkerIconType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.SavedLocation

sealed interface SavedLocationsEvent {
    object OpenCreateModal : SavedLocationsEvent
    object DismissCreateModal : SavedLocationsEvent
    data class OpenEditModal(val location: SavedLocation) : SavedLocationsEvent
    data class DeleteSavedLocation(val location: SavedLocation) : SavedLocationsEvent
    data class AddSavedLocation(val name: String, val icon: MarkerIconType, val position: Position) : SavedLocationsEvent
    data class UpdateSavedLocation(val id: Int, val name: String, val icon: MarkerIconType, val position: Position) : SavedLocationsEvent
}