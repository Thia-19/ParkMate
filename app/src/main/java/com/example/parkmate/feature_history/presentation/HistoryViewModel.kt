package com.example.parkmate.feature_history.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.MockData
import com.example.parkmate.core.domain.use_case.parking_sessions.ParkingSessionsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCases: ParkingSessionsUseCases
) : ViewModel() {

    // TRUE private val _state = MutableStateFlow(HistoryState())
    //da cancellare sotto
    private val initialMockSession = MockData.mockSessions.filter { !it.isActive }
    private val _state = MutableStateFlow(
        HistoryState(
            allSession = initialMockSession,
            sessions = initialMockSession, // <-- Inserisci i mock qui
            userVehicles = MockData.mockVehicles
        )
    )
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private var getSessionsJob: Job? = null

    init {
        // observeParkingSessions(), togli il commento quando lavori con dati veri
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.OnTabSelected -> {
                _state.update { currentState ->
                    currentState.copy(selectedTab = event.tab)
                }
            }

            is HistoryEvent.FilterByVehicle -> {
                _state.update { it.copy(selectedVehicleId = event.vehicleId) }
                applyFilters()
            }

            is HistoryEvent.FilterByParkType -> {
                _state.update { it.copy(selectedParkingType = event.parkType) }
                applyFilters()
            }

            is HistoryEvent.FilterByDateRange -> {
                _state.update {
                    it.copy(
                        selectedStartDate = event.startDate,
                        selectedEndDate = event.endDate
                    )
                }
                applyFilters()
            }

            is HistoryEvent.ResetFilters -> {
                _state.update {
                    it.copy(
                        selectedVehicleId = null,
                        selectedParkingType = null,
                        selectedEndDate = null,
                        selectedStartDate = null
                    )
                }
                applyFilters()
            }

            is HistoryEvent.DeleteSession -> {
                viewModelScope.launch {
                    useCases.deleteParkingSession(event.session)
                }
            }

            is HistoryEvent.OnSessionClick -> {
                viewModelScope.launch {
                    // to do
                }
            }
        }
    }

    private fun observeParkingSessions() {
        // Annulla l'ascolto precedente se presente
        getSessionsJob?.cancel()
        _state.update { it.copy(isLoading = true) }
        getSessionsJob = useCases.getParkingSessions()
            .onEach { allSessions ->

                val concludedSessions = allSessions.filter { !it.isActive }
                _state.update { currentState ->
                    currentState.copy(
                        allSession = concludedSessions,
                        sessions = concludedSessions,
                        isLoading = false
                    )
                }
                applyFilters()
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Unknown error"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun applyFilters() {
        _state.update { currentState ->
            val filteredList = currentState.allSession.filter { session ->

                // 1. Filtro Veicolo (se null -> accetta tutti)
                val matchesVehicle = currentState.selectedVehicleId == null ||
                        session.vehicleId == currentState.selectedVehicleId

                // 2. Filtro Tipo Parcheggio (se null -> accetta tutti)
                val matchesType = currentState.selectedParkingType == null ||
                        session.parkingType == currentState.selectedParkingType

                // 3. Filtro Range Date
                // Controlla se il parcheggio rientra nell'intervallo selezionato
                val matchesDate = when {
                    currentState.selectedStartDate == null && currentState.selectedEndDate == null -> true
                    currentState.selectedStartDate != null && currentState.selectedEndDate != null -> {
                        // Il parcheggio è valido se il suo startTime rientra tra startDate e endDate
                        session.startTime in currentState.selectedStartDate..currentState.selectedEndDate
                    }
                    currentState.selectedStartDate != null -> {
                        session.startTime >= currentState.selectedStartDate
                    }
                    else -> true
                }

                // La sessione deve soddisfare TUTTI e 3 i criteri
                matchesVehicle && matchesType && matchesDate
            }

            currentState.copy(sessions = filteredList)
        }
    }
}