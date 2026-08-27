package com.example.parkmate.feature_parkingEffort.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.parkingEffort.RankedParkingZone
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.use_case.parking_effort.GenerateParkingZonesUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.GetNearestParkingZoneUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.GetTopParkingZonesUseCase
import com.example.parkmate.notifications.NotificationsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ParkingEffortUiState(
    val zones: List<ParkingZone> = emptyList(),
    val topZones: List<RankedParkingZone> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class ParkingEffortViewModel @Inject constructor(
    // NOTIFICA
    @ApplicationContext private val context: Context,

    effortRepository: ParkingEffortRepository,
    private val generateParkingZones: GenerateParkingZonesUseCase,
    private val getTopParkingZones: GetTopParkingZonesUseCase,
    private val getNearestParkingZoneUseCase: GetNearestParkingZoneUseCase
) : ViewModel() {

    val state: StateFlow<ParkingEffortUiState> = effortRepository.getEffortMapPoints()
        .map { efforts ->
            val zones = generateParkingZones(efforts)
            if (zones.isEmpty()) {
                return@map ParkingEffortUiState(isLoading = false)
            }
            // NOTIFICA
            NotificationsHelper.showRouteSavedNotification(context)

            val topZones = getTopParkingZones(zones)
            ParkingEffortUiState(
                zones = zones,
                topZones = topZones,
                isLoading = false
            )
        }
        .catch { error ->
            emit(
                ParkingEffortUiState(
                    isLoading = false,
                    errorMessage = error.message ?: "Impossibile caricare le zone di parcheggio."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ParkingEffortUiState(isLoading = true)
        )

    fun getNearestZone(latitude: Double, longitude: Double): ParkingZone? {
        val currentZones = state.value.zones
        return getNearestParkingZoneUseCase(currentZones, latitude, longitude)
    }
}


