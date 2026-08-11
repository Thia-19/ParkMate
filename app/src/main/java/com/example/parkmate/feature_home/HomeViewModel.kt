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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: ParkingSessionsUseCases
) : ViewModel(){
    private val initialMockSession = MockData.mockSessions.filter { it.isActive }
    private val _state = MutableStateFlow(
        HistoryState(
            allSession = initialMockSession,
            sessions = initialMockSession,
        )
    )
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private var getSessionsJob: Job? = null

    init {
        // observeParkingSessions(), togli il commento quando lavori con dati veri
    }

    fun onEvent(event: HomeEvent) {
        // TODO
    }
}
