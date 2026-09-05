package com.example.parkmate.feature_savedLocations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkmate.core.domain.models.MarkerIconType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.SavedLocation
import com.example.parkmate.core.domain.use_case.saved_locations.SavedLocationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsViewModel @Inject constructor(
    private val useCases: SavedLocationUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(SavedLocationsState())
    val state: StateFlow<SavedLocationsState> = _state
        .onStart { loadLocations() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SavedLocationsState(isLoading = true)
        )

    private fun loadLocations(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                useCases.getAllSavedLocations().collect { list ->
                    _state.update {
                        it.copy(
                            locations = list,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception){
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Errore nel caricamento"
                    )
                }
            }
        }
    }

    fun onEvent(event: SavedLocationsEvent){
        when(event){
            is SavedLocationsEvent.AddSavedLocation -> {
                createSavedLocation(
                    name = event.name,
                    icon = event.icon,
                    position = event.position
                )
            }
            is SavedLocationsEvent.DeleteSavedLocation -> {
                viewModelScope.launch {
                    useCases.deleteSavedLocation(event.location)
                }
            }
            is SavedLocationsEvent.UpdateSavedLocation -> {
                val locationToEdit = state.value.locationToEdit
                if (locationToEdit != null) {
                    val updatedLocation = locationToEdit.copy(
                        name = event.name,
                        icon = event.icon,
                        position = event.position
                    )

                    viewModelScope.launch {
                        useCases.updateSavedLocation(updatedLocation)
                        _state.update { it.copy(locationToEdit = null) }
                    }
                }
            }
            is SavedLocationsEvent.OpenCreateModal -> {
                _state.update { it.copy(isCreateModalOpen = true) }
            }
            is SavedLocationsEvent.OpenEditModal -> {
                _state.update {
                    it.copy(locationToEdit = event.location)
                }
            }

            is SavedLocationsEvent.DismissCreateModal -> {
                _state.update {
                    it.copy(
                        isCreateModalOpen = false,
                        locationToEdit = null
                    )
                }
            }
        }
    }

    fun createSavedLocation(name: String, icon: MarkerIconType, position: Position) {
        viewModelScope.launch {
            _state.update { it.copy(isCreateModalOpen = false, isLoading = true) }

            try {
                val newLocation = SavedLocation(
                    id = 0,
                    name = name,
                    icon = icon,
                    position = position
                )
                useCases.addSavedLocation(newLocation)
                _state.update { it.copy(isCreateModalOpen = false, isLoading = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Impossibile salvare la posizione"
                    )
                }
            }
        }
    }
}