package com.example.parkmate.feature_parkingEffort.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.use_case.parking_effort.GenerateParkingZonesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ParkingEffortState(
    val zones: List<ParkingZone> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ParkingEffortViewModel @Inject constructor(
    effortRepository: ParkingEffortRepository,
    private val generateParkingZonesUseCase: GenerateParkingZonesUseCase
) : ViewModel() {

    val state: StateFlow<ParkingEffortState> = effortRepository.getEffortMapPoints()
        .map { mapPoints ->

            val zones = generateParkingZonesUseCase(efforts = mapPoints)

            ParkingEffortState(
                zones = zones,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ParkingEffortState(isLoading = true)
        )
}