package com.example.parkmate.feature_settings

data class SettingsState (
    val currentPosition: Boolean = true,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = ""
)