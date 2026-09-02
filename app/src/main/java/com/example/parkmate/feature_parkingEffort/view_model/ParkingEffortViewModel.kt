package com.example.parkmate.feature_parkingEffort.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.BackgroundTaskManager
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.parkingEffort.RankedParkingZone
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.use_case.parking_effort.CalculateParkingScoreUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.GenerateParkingZonesUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.GetNearestParkingZoneUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.GetTopParkingZonesUseCase
import com.example.parkmate.core.domain.util.observers.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParkingEffortUiState(
    val zones: List<ParkingZone> = emptyList(),
    val topZones: List<RankedParkingZone> = emptyList(),
    val isLoading: Boolean = true,
    val isConnected: Boolean = true,
    val activeTrackingSessionId: Int? = null,
    val errorMessageResId: Int? = null
)

@HiltViewModel
class ParkingEffortViewModel @Inject constructor(
    private val effortRepository: ParkingEffortRepository,
    private val calculateParkingScoreUseCase: CalculateParkingScoreUseCase,
    private val generateParkingZones: GenerateParkingZonesUseCase,
    private val getTopParkingZones: GetTopParkingZonesUseCase,
    private val getNearestParkingZoneUseCase: GetNearestParkingZoneUseCase,
    private val backgroundTaskManager: BackgroundTaskManager,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val state: StateFlow<ParkingEffortUiState> = combine(
        effortRepository.getEffortMapPoints(),
        connectivityObserver.observe()
    ) { efforts, isConnected ->
        val zones = generateParkingZones(efforts)
        if (zones.isEmpty()) {
            return@combine ParkingEffortUiState(isLoading = false, isConnected = isConnected)
        }

        val topZones = getTopParkingZones(zones)
        ParkingEffortUiState(
            zones = zones,
            topZones = topZones,
            isLoading = false,
            isConnected = isConnected
        )
    }
        .catch {
            emit(
                ParkingEffortUiState(
                    isLoading = false,
                    errorMessageResId = R.string.pe_error_loading_zones
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ParkingEffortUiState(isLoading = true)
        )


    fun startRouteTracking(sessionId: Int) {
        backgroundTaskManager.startRouteTracking(sessionId)
    }

    fun stopAndSaveRouteTracking(sessionId: Int) {
        backgroundTaskManager.stopRouteTracking(sessionId)
    }

    fun cancelRouteTracking(sessionId: Int) {
        backgroundTaskManager.cancelRouteTracking(sessionId)
    }

    fun resumeRouteTracking(sessionId: Int) {
        backgroundTaskManager.resumeTracking(sessionId)
    }


    fun getNearestZone(latitude: Double, longitude: Double): ParkingZone? {
        val currentZones = state.value.zones
        return getNearestParkingZoneUseCase(currentZones, latitude, longitude)
    }
}


