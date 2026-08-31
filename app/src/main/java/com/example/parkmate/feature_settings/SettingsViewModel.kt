package com.example.parkmate.feature_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.core.preferences.domain.use_case.SettingsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCases: SettingsUseCases
) : ViewModel(){
    val state: StateFlow<SettingsState> = combine(
        useCases.getActivityRecognitionHome(),
        useCases.getUseCurrentPosition(),
    ) { settings ->
        SettingsState(
            activityRecognitionHome = settings[0],
            currentPosition = settings[1]
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsState()
    )

    fun onEvent(event: SettingsEvent){
        when(event){
            is SettingsEvent.SetCurrentPosition -> {
                viewModelScope.launch {
                    useCases.setUseCurrentPosition(event.currentPosition)
                }
            }
            is SettingsEvent.SetActivityRecognitionHome -> {
                viewModelScope.launch {
                    useCases.setActivityRecognitionHome(event.AR_Home)
                }
            }
        }
    }
}