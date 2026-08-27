package com.example.parkmate.feature_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.use_case.parking_sessions.ParkingSessionsUseCases
import com.example.parkmate.core.domain.use_case.saved_locations.SavedLocationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: ParkingSessionsUseCases,
    private val savedLocationsUseCases: SavedLocationUseCases
) : ViewModel(){

    val state: StateFlow<HomeState> = combine(
        useCases.getActiveParkingSessions(),
        savedLocationsUseCases.getAllSavedLocations()
    ){ sessions, savedLocations ->
        HomeState (
            sessions = sessions,
            savedLocations = savedLocations,
            isLoading = false,
            errorMessage = null
        )
    }
        .catch { e ->
            emit(
                HomeState (
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Unknown error"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeState(isLoading = true)
        )



    fun onEvent(event: HomeEvent) {
        when(event){
            is HomeEvent.AddSession -> {
                viewModelScope.launch {
                    useCases.addParkingSession(event.session)
                }
            }
            is HomeEvent.CloseActiveSession -> {
                viewModelScope.launch {
                    useCases.closeActiveParkingSession(event.session)
                }
            }
            is HomeEvent.DeleteSession -> {
                viewModelScope.launch {
                    useCases.deleteParkingSession(event.session)
                }
            }
            is HomeEvent.OnSessionClick -> {
                viewModelScope.launch {
                    // to do
                }
            }
        }
    }
}


