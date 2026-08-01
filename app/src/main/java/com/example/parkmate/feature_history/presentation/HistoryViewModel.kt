package com.example.parkmate.feature_history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.use_case.parking_sessions.GetAllParkingSessions
import com.example.parkmate.core.domain.use_case.parking_sessions.ParkingSessionsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Unico ViewModel che gestisce lo stato di tutta la feature
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCases: ParkingSessionsUseCases
) : ViewModel () {
    private val _state = MutableStateFlow(HistoryState())

    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        loadParkingSessions()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.ChangeViewMode -> {
                _state.update { it.copy(viewMode = event.viewMode) }
            }
            is HistoryEvent.FilterByVehicle -> {
                _state.update { it.copy(selectedVehicleId = event.vehicleId?.toString()) }
                applyFilters()
            }
            is HistoryEvent.FilterByParkType -> {
                _state.update { it.copy(selectedParkType = event.parkType) }
                applyFilters()
            }
            is HistoryEvent.FilterByDateRange -> {
                _state.update { it.copy(dateRange = event.dateRange) }
                applyFilters()
            }
            is HistoryEvent.ResetFilters -> {
                _state.update {
                    it.copy(
                        selectedVehicleId = null,
                        selectedParkType = null,
                        dateRange = null
                    )
                }
                loadParkingSessions()
            }
            is HistoryEvent.DeleteSession -> {
                viewModelScope.launch {
                    useCases.deleteParkingSession(event.session)
                    loadParkingSessions()
                }
            }
        }
    }

    private fun loadParkingSessions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Esempio: recupera dal UseCase
                useCases.getParkingSessions().collect { sessions ->
                    _state.update {
                        it.copy(
                            parkingSessions = sessions,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Errore sconosciuto"
                    )
                }
            }
        }
    }

    private fun applyFilters() {
        // Qui gestisci la logica di filtraggio locale o tramite Use Case
    }


}