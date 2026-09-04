package com.example.parkmate.feature_parkingEffort.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.parkingEffort.RankedParkingZone
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.use_case.parking_effort.GenerateParkingZonesUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.GetNearestParkingZoneUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.GetTopParkingZonesUseCase
import com.example.parkmate.core.domain.util.observers.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface ParkingEffortUiState {
    data object Loading : ParkingEffortUiState

    data class Success(
        val zones: List<ParkingZone>,
        val topZones: List<RankedParkingZone>,
        val isConnected: Boolean
    ) : ParkingEffortUiState

    data class Error(val messageResId: Int) : ParkingEffortUiState
}

sealed interface ParkingEffortEvent {
    data class OperationFailed(val error: Throwable) : ParkingEffortEvent
}

@HiltViewModel
class ParkingEffortViewModel @Inject constructor(
    private val effortRepository: ParkingEffortRepository,
    private val generateParkingZones: GenerateParkingZonesUseCase,
    private val getTopParkingZones: GetTopParkingZonesUseCase,
    private val getNearestParkingZoneUseCase: GetNearestParkingZoneUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val state: StateFlow<ParkingEffortUiState> = combine(
        effortRepository.getEffortMapPoints(),
        connectivityObserver.observe()
    ) { efforts, isConnected ->
        val zones = generateParkingZones(efforts)
        val topZones = if (zones.isEmpty()) emptyList() else getTopParkingZones(zones)

        val successState: ParkingEffortUiState = ParkingEffortUiState.Success(
            zones = zones,
            topZones = topZones,
            isConnected = isConnected
        )
        successState
    }
        .flowOn(Dispatchers.Default)
        .catch {
            emit(ParkingEffortUiState.Error(R.string.pe_error_loading_zones))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ParkingEffortUiState.Loading
        )

    private val _events = Channel<ParkingEffortEvent>(capacity = Channel.BUFFERED)
    val events: Flow<ParkingEffortEvent> = _events.receiveAsFlow()


    fun getNearestZone(latitude: Double, longitude: Double): ParkingZone? {
        val currentZones = (state.value as? ParkingEffortUiState.Success)?.zones.orEmpty()
        return getNearestParkingZoneUseCase(currentZones, latitude, longitude)
    }

}

