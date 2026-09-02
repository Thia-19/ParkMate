package com.example.parkmate.feature_vehiclesManager.viewModel

import android.net.Uri
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

    private val _filterState = MutableStateFlow(VehicleFilterState())
    val filterState: StateFlow<VehicleFilterState> = _filterState.asStateFlow()

    val vehicles: StateFlow<List<ParkedVehicle>> = combine(
        getVehiclesWithParkingSessionUseCase(),
        _searchQuery,
        _filterState
    ) { vehiclesFromDb, query, filters ->

        vehiclesFromDb.filter { parkedVehicle ->

            val matchesSearch =
                query.isBlank() ||
                        parkedVehicle.vehicle.name.contains(
                            query,
                            ignoreCase = true
                        )

            val matchesStatus = when (filters.status) {
                VehicleStatusFilter.ALL -> true

                VehicleStatusFilter.AVAILABLE ->
                    !parkedVehicle.isParked

                VehicleStatusFilter.PARKED ->
                    parkedVehicle.isParked
            }

            val matchesType =
                filters.type == null ||
                        parkedVehicle.vehicle.type == filters.type

            matchesSearch && matchesStatus && matchesType
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: VehicleStatusFilter) {
        _filterState.value = _filterState.value.copy(
            status = status
        )
    }

    fun setTypeFilter(type: VehicleType?) {
        _filterState.value = _filterState.value.copy(
            type = type
        )
    }

    fun saveVehicle(
        vehicle: Vehicle,
        imageUri: Uri? = null
    ) {
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