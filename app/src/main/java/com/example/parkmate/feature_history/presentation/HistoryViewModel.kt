package com.example.parkmate.feature_history.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.use_case.parking_sessions.ParkingSessionsUseCases
import com.example.parkmate.core.domain.use_case.vehicles.GetVehicleByIdUseCase
import com.example.parkmate.core.domain.use_case.vehicles.GetVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private data class FilterState(
    val tab: HistoryTab = HistoryTab.LIST,
    val vehicleId: Int? = null,
    val parkType: ParkingType? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCases: ParkingSessionsUseCases,
    private val getVehicles: GetVehiclesUseCase,
) : ViewModel() {

    private val _filterState = MutableStateFlow(FilterState())
    private val _concludedSessions = useCases.getConcludedParkingSessions()
    private val _userVehicles = getVehicles()

    val state: StateFlow<HistoryState> = combine(
        _concludedSessions,
        _filterState,
        _userVehicles
    ) { sessionsWithVehicle, filters, vehicles ->

        val filteredList = sessionsWithVehicle.filter { item ->
            val session = item.session

            val matchesVehicle = filters.vehicleId == null || session.vehicleId == filters.vehicleId
            val matchesType = filters.parkType == null || session.parkingType == filters.parkType
            val matchesDate = when {
                filters.startDate == null && filters.endDate == null -> true
                filters.startDate != null && filters.endDate != null -> session.startTime in filters.startDate..filters.endDate
                filters.startDate != null -> session.startTime >= filters.startDate
                else -> true
            }
            matchesVehicle && matchesType && matchesDate
        }

        HistoryState(
            allSessions = sessionsWithVehicle,
            userVehicles = vehicles,
            sessions = filteredList,
            selectedTab = filters.tab,
            selectedVehicleId = filters.vehicleId,
            selectedParkingType = filters.parkType,
            selectedStartDate = filters.startDate,
            selectedEndDate = filters.endDate,
            isLoading = false
        )
    }
        .catch { e ->
            emit(HistoryState(isLoading = false, errorMessage = e.localizedMessage ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryState(isLoading = true)
        )

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.OnTabSelected -> {
                _filterState.update { it.copy(tab = event.tab) }
            }
            is HistoryEvent.FilterByVehicle -> {
                _filterState.update { it.copy(vehicleId = event.vehicleId) }
            }
            is HistoryEvent.FilterByParkType -> {
                _filterState.update { it.copy(parkType = event.parkType) }
            }
            is HistoryEvent.FilterByDateRange -> {
                _filterState.update { it.copy(startDate = event.startDate, endDate = event.endDate) }
            }
            is HistoryEvent.ResetFilters -> {
                _filterState.value = FilterState()
            }
            is HistoryEvent.DeleteSession -> {
                viewModelScope.launch {
                    useCases.deleteParkingSession(event.session)
                }
            }
            is HistoryEvent.OnSessionClick -> {
                viewModelScope.launch { /* to do */ }
            }
        }
    }
}