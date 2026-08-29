package com.example.parkmate.core.preferences.domain.use_case

data class SettingsUseCases(
    val getUseCurrentPosition: GetUseCurrentPositionUseCase,
    val setUseCurrentPosition: SetUseCurrentPositionUseCase,
    val getActivityRecognitionHome: GetActivityRecognitionHomeUseCase,
    val setActivityRecognitionHome: SetActivityRecognitionHomeUseCase
)