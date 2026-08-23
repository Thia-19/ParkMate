package com.example.parkmate.feature_vehiclesManager.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.use_case.vehicles.DeleteVehicleUseCase
import com.example.parkmate.core.domain.use_case.vehicles.GetVehiclesWithParkingSessionUseCase
import com.example.parkmate.core.domain.use_case.vehicles.SaveVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    val vehicles: StateFlow<List<ParkedVehicle>> = combine(
        getVehiclesWithParkingSessionUseCase(),
        _searchQuery
    ) { vehiclesFromDb, query ->
        if (query.isBlank()) {
            vehiclesFromDb
        } else {
            vehiclesFromDb.filter { parkedVehicle ->
                parkedVehicle.vehicle.name.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun saveVehicle(vehicle: Vehicle, imageUri: Uri? = null) {
        viewModelScope.launch {
            val uriString = imageUri?.toString()

            val isUpdate = vehicle.id != 0

            saveVehicleUseCase(
                vehicle = vehicle,
                newImageUriString = uriString,
                isUpdate = isUpdate
            )
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            deleteVehicleUseCase(vehicle)
        }
    }
}