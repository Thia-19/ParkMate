package com.example.parkmate.feature_settings

sealed interface SettingsEvent {
    data class SetCurrentPosition (val currentPosition: Boolean) : SettingsEvent
    data class SetActivityRecognitionHome (val AR_Home: Boolean) : SettingsEvent
}