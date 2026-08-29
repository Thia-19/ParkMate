package com.example.parkmate.feature_settings

sealed interface SettingsEvent {
    /* TODO */
    data class SetCurrentPosition (val currentPosition: Boolean) : SettingsEvent
    data class SetActivityRecognitionHome (val AR_Home: Boolean) : SettingsEvent
    // data object OnSaveSettings : SettingsEvent
}