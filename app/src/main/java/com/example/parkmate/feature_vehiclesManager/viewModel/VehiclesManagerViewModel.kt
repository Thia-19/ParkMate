package com.example.parkmate.feature_vehiclesManager.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.models.vehicle.VehicleType
import com.example.parkmate.core.domain.use_case.vehicles.DeleteVehicleUseCase
import com.example.parkmate.core.domain.use_case.vehicles.GetVehiclesWithParkingSessionUseCase
import com.example.parkmate.core.domain.use_case.vehicles.SaveVehicleUseCase
import com.example.parkmate.feature_vehiclesManager.viewModel.util.VehicleFilterState
import com.example.parkmate.feature_vehiclesManager.viewModel.util.VehicleStatusFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehiclesManagerViewModel @Inject constructor(
    getVehiclesWithParkingSessionUseCase: GetVehiclesWithParkingSessionUseCase,
    private val saveVehicleUseCase: SaveVehicleUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterState = MutableStateFlow(VehicleFilterState())
    val filterState: StateFlow<VehicleFilterState> = _filterState.asStateFlow()

    private val _uiEvent = Channel<VehicleUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<VehicleUiEvent> = _uiEvent.receiveAsFlow()

    private val debouncedQuery = _searchQuery
        .debounce(SEARCH_DEBOUNCE_MILLIS)
        .distinctUntilChanged()

    val uiState: StateFlow<VehiclesUiState> = combine(
        getVehiclesWithParkingSessionUseCase(),
        debouncedQuery,
        _filterState
    ) { vehiclesFromDb, query, filters ->
        VehiclesUiState.Success(vehiclesFromDb.filterBy(query, filters)) as VehiclesUiState
    }
        .flowOn(Dispatchers.Default)
        .catch { throwable ->
            emit(VehiclesUiState.Error(throwable.message ?: "Errore nel caricamento dei veicoli"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = VehiclesUiState.Loading
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: VehicleStatusFilter) {
        _filterState.update { it.copy(status = status) }
    }

    fun setTypeFilter(type: VehicleType?) {
        _filterState.update { it.copy(type = type) }
    }

    fun saveVehicle(vehicle: Vehicle, imageUriString: String? = null) {
        viewModelScope.launch {
            runCatchingUiAction(fallbackMessage = "Impossibile salvare il veicolo") {
                saveVehicleUseCase(
                    vehicle = vehicle,
                    newImageUriString = imageUriString,
                    isUpdate = vehicle.id != 0
                )
            }
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            runCatchingUiAction(fallbackMessage = "Impossibile eliminare il veicolo") {
                deleteVehicleUseCase(vehicle)
                _uiEvent.send(VehicleUiEvent.VehicleDeleted(vehicle))
            }
        }
    }

    fun buildVehicleToSave(
        existingVehicle: Vehicle?,
        name: String,
        type: VehicleType
    ): Vehicle = existingVehicle?.copy(name = name, type = type)
        ?: Vehicle(name = name, type = type)


    private suspend fun runCatchingUiAction(
        fallbackMessage: String,
        action: suspend () -> Unit
    ) {
        try {
            action()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiEvent.send(VehicleUiEvent.ShowMessage(e.message ?: fallbackMessage))
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 300L
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}

private fun List<ParkedVehicle>.filterBy(
    query: String,
    filters: VehicleFilterState
): List<ParkedVehicle> = filter { parkedVehicle ->
    val matchesSearch =
        query.isBlank() || parkedVehicle.vehicle.name.contains(query, ignoreCase = true)

    val matchesStatus = when (filters.status) {
        VehicleStatusFilter.ALL -> true
        VehicleStatusFilter.AVAILABLE -> !parkedVehicle.isParked
        VehicleStatusFilter.PARKED -> parkedVehicle.isParked
    }

    val matchesType = filters.type == null || parkedVehicle.vehicle.type == filters.type

    matchesSearch && matchesStatus && matchesType
}

sealed interface VehiclesUiState {
    data object Loading : VehiclesUiState
    data class Success(val vehicles: List<ParkedVehicle>) : VehiclesUiState
    data class Error(val message: String) : VehiclesUiState
}

sealed interface VehicleUiEvent {
    data class ShowMessage(val message: String) : VehicleUiEvent
    data class VehicleDeleted(val vehicle: Vehicle) : VehicleUiEvent
}