package com.example.parkmate.feature_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.preferences.domain.use_case.SettingsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCases: SettingsUseCases
) : ViewModel(){
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private var observeSettingsJob: Job? = null

    init {
        observeSettings()
    }

    fun onEvent(event: SettingsEvent){
        when(event){
            is SettingsEvent.SetCurrentPosition -> {
                _state.update { it.copy(currentPosition = event.currentPosition) }
            }
            SettingsEvent.OnSaveSettings -> {
                saveSettings()
            }
        }
    }

    private fun observeSettings () {
        observeSettingsJob?.cancel()
        _state.update { it.copy(isLoading = true) }

        observeSettingsJob = useCases.getUseCurrentPosition()
            .onEach { useCurrentPosition ->
                _state.update {
                    it.copy(
                        currentPosition = useCurrentPosition,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun saveSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                useCases.setUseCurrentPosition(_state.value.currentPosition)
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Errore nel salvataggio"
                    )
                }
            }
        }
    }
}