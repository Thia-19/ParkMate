package com.example.parkmate.di

import com.example.parkmate.core.data.local.data_source.ParkingEffortDao
import com.example.parkmate.core.data.repository.ParkingEffortRepositoryImpl
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ParkingEffortModule {

    @Provides
    @Singleton
    fun provideParkingEffortRepository(
        dao: ParkingEffortDao
    ): ParkingEffortRepository {
        return ParkingEffortRepositoryImpl(dao)
    }



}