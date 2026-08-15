package com.example.parkmate.feature_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.MockData
import com.example.parkmate.core.domain.use_case.parking_sessions.ParkingSessionsUseCases
import com.example.parkmate.feature_history.presentation.HistoryEvent
import com.example.parkmate.feature_history.presentation.HistoryState
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
class HomeViewModel @Inject constructor(
    private val useCases: ParkingSessionsUseCases
) : ViewModel(){
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var getSessionsJob: Job? = null

    init {
        observeParkingSessions()
    }

    fun onEvent(event: HomeEvent) {
        when(event){
            is HomeEvent.AddSession -> {
                viewModelScope.launch {
                    useCases.addParkingSession(event.session)
                }
            }
            is HomeEvent.CloseActiveSession -> {
                viewModelScope.launch {
                    useCases.closeActiveParkingSession(event.session.vehicleId)
                }
            }
            is HomeEvent.DeleteSession -> {
                viewModelScope.launch {
                    useCases.deleteParkingSession(event.session)
                }
            }
            HomeEvent.RefreshSessions -> {
                observeParkingSessions()
            }
        }
    }

    private fun observeParkingSessions() {
        getSessionsJob?.cancel()
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        getSessionsJob = useCases.getParkingSessions()
            .onEach { sessions ->
                _state.update {
                    it.copy(
                        sessions = sessions,
                        isLoading = false
                    )
                }
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
}


