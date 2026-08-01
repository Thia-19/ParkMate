package com.example.parkmate.di
import com.example.parkmate.core.data.local.data_source.ParkingSessionDao
import com.example.parkmate.core.data.repository.ParkingSessionsRepositoryImpl
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.core.domain.use_case.parking_sessions.DeleteParkingSession
import com.example.parkmate.core.domain.use_case.parking_sessions.GetAllParkingSessions
import com.example.parkmate.core.domain.use_case.parking_sessions.ParkingSessionsUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HistoryModule {

    @Provides
    @Singleton
    fun provideParkingSessionsRepository(
        dao: ParkingSessionDao
    ): ParkingSessionRepository {
        return ParkingSessionsRepositoryImpl(dao)
    }

    // inietto solo gli use case di cui history ha bisogno

    @Provides
    @Singleton
    fun provideGetParkingSessionsUseCase(
        repository: ParkingSessionRepository
    ): GetAllParkingSessions {
        return GetAllParkingSessions(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteParkingSessionsUseCase(
        repository: ParkingSessionRepository
    ): DeleteParkingSession {
        return DeleteParkingSession(repository)
    }
}
