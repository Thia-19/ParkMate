package com.example.parkmate.core.preferences.di

import android.content.Context
import com.example.parkmate.core.preferences.data.SettingsDataStore
import com.example.parkmate.core.preferences.data.SettingsRepositoryImpl
import com.example.parkmate.core.preferences.domain.SettingsRepository
import com.example.parkmate.core.preferences.domain.use_case.GetActivityRecognitionHomeUseCase
import com.example.parkmate.core.preferences.domain.use_case.GetUseCurrentPositionUseCase
import com.example.parkmate.core.preferences.domain.use_case.SetActivityRecognitionHomeUseCase
import com.example.parkmate.core.preferences.domain.use_case.SetUseCurrentPositionUseCase
import com.example.parkmate.core.preferences.domain.use_case.SettingsUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(dataStore: SettingsDataStore): SettingsRepository {
        return SettingsRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideSettingsUseCases(repository: SettingsRepository): SettingsUseCases {
        return SettingsUseCases(
            getUseCurrentPosition = GetUseCurrentPositionUseCase(repository),
            setUseCurrentPosition = SetUseCurrentPositionUseCase(repository),
            getActivityRecognitionHome = GetActivityRecognitionHomeUseCase(repository),
            setActivityRecognitionHome = SetActivityRecognitionHomeUseCase(repository),
        )
    }
}