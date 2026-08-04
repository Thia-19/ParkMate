package com.example.parkmate.feature_history.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    // Mantieni il riferimento al Job del Flow per evitare collezionatori multipli
    private var getSessionsJob: Job? = null

    init {
        observeParkingSessions()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.ChangeViewMode -> {
                _state.update { currentState ->
                    val newMode = if (currentState.viewMode == HistoryViewMode.LIST) {
                        HistoryViewMode.MAP
                    } else {
                        HistoryViewMode.LIST
                    }
                    currentState.copy(viewMode = newMode)
                }
            }

            is HistoryEvent.FilterByVehicle -> {
                _state.update { it.copy(selectedVehicleId = event.vehicleId) }
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
                applyFilters()
            }

            is HistoryEvent.DeleteSession -> {
                viewModelScope.launch {
                    useCases.deleteParkingSession(event.session)
                    // Non serve richiamare observeParkingSessions():
                    // Room aggiornerà automaticamente il Flow!
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
            .onEach { sessions ->
                _state.update { currentState ->
                    currentState.copy(
                        sessions = sessions,
                        // Se vuoi tenere anche una lista filtrata visibile nell'UI:
                        // filteredSessions = filterList(sessions, currentState),
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Errore sconosciuto"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun applyFilters() {
        // Esempio di filtraggio locale se hai sia `parkingSessions` (lista grezza dal DB)
        // che `filteredSessions` (lista filtrata mostrata a schermo) nello stato:
        /*
        _state.update { currentState ->
            val filtered = currentState.parkingSessions.filter { session ->
                val matchesVehicle = currentState.selectedVehicleId == null || session.vehicleId.toString() == currentState.selectedVehicleId
                val matchesType = currentState.selectedParkType == null || session.parkType == currentState.selectedParkType
                // aggiungi condizione dateRange...
                matchesVehicle && matchesType
            }
            currentState.copy(filteredSessions = filtered)
        }
        */
    }
}