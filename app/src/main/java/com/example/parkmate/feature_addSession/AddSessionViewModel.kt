package com.example.parkmate.feature_addSession

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.use_case.parking_sessions.ParkingSessionsUseCases
import com.example.parkmate.core.preferences.domain.use_case.GetUseCurrentPositionUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSessionViewModel @Inject constructor(
    private val useCases: ParkingSessionsUseCases,
    private val getUseCurrentPositionUseCase: GetUseCurrentPositionUseCase,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _state = MutableStateFlow(AddSessionState())
    val state: StateFlow<AddSessionState> = _state.asStateFlow()

    init {
        fetchLocationIfEnabled()
    }

    fun onEvent(event: AddSessionEvent){
        when (event) {
            is AddSessionEvent.OnVehicleSelected -> {
                _state.update { it.copy(vehicleId = event.vehicleId) }
            }
            is AddSessionEvent.OnLocationSelected -> {
                _state.update { it.copy(locationId = event.locationId) }
            }
            is AddSessionEvent.OnNoteChanged -> {
                _state.update { it.copy(note = event.note) }
            }
            is AddSessionEvent.OnParkingTypeChanged -> {
                _state.update { it.copy (parkingType = event.type) }
            }
            is AddSessionEvent.OnHourlyRateChanged -> {
                _state.update { it.copy (hourlyRate = event.rate)}
            }

            is AddSessionEvent.OnFixedCostChanged -> {
                _state.update { it.copy(fixedCost = event.cost)}
            }

            is AddSessionEvent.OnExpiryTimeChanged -> {
                _state.update { it.copy(expiryTime = event.time) }
            }
            is AddSessionEvent.OnPhotoCaptured -> {
                _state.update { it.copy(photoUri = event.uri) }
            }
            is AddSessionEvent.OnPositionFetched -> {
                _state.update { it.copy(position = event.position) }
            }
            AddSessionEvent.FetchLocationRequested -> {
                fetchLocationIfEnabled()
            }
            AddSessionEvent.OnSaveSession -> {
                saveSession()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocationIfEnabled() {
        viewModelScope.launch {
            val isLocationEnabled = getUseCurrentPositionUseCase().first()
            if (!isLocationEnabled) return@launch

            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            _state.update {
                                it.copy(
                                    position = Position(
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                    )
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(errorMessage = "Unable to obtain current GPS location")
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        _state.update {
                            it.copy(errorMessage = exception.localizedMessage ?: "GPS Error")
                        }
                    }
            } catch (e: SecurityException) {
                _state.update { it.copy(errorMessage = "Position permissions not enables.") }
            }
        }
    }

    private fun saveSession (){
        val currentState = _state.value

        /* TODO
        val vehicleId = currentState.vehicleId ?: run {
            _state.update { it.copy(errorMessage = "Select a vehicle!") }
            return
        }
        */

        val selectedSavedLocation = currentState.locations.find { it.id == currentState.locationId }
        val finalPosition = selectedSavedLocation?.position ?: currentState.position

        if (finalPosition == null) {
            _state.update { it.copy(errorMessage = "Position not available") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val newSession = ParkingSession(
                    id = 0,
                    isActive = true,
                    vehicleId = (1..100).random(), // TEST; TEST; TEST; TODO
                    parkingType = currentState.parkingType,
                    startTime = System.currentTimeMillis(),
                    position = finalPosition,

                    hourlyRate = if (currentState.parkingType == ParkingType.HOURLY_PAID) currentState.hourlyRate else null,
                    fixedCost = if (currentState.parkingType == ParkingType.FIXED_TICKET) currentState.fixedCost else null,
                    expiryTime = if (currentState.parkingType == ParkingType.FIXED_TICKET) currentState.expiryTime else null,

                    note = currentState.note?.ifBlank { null },
                    photoUri = currentState.photoUri,

                    endTime = null,
                    finalCost = null
                )

                useCases.addParkingSession(newSession)
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Errore durante il salvataggio"
                    )
                }
            }
        }
    }
}