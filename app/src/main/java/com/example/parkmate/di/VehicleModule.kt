package com.example.parkmate.di

import android.content.Context
import com.example.parkmate.core.data.local.data_source.VehicleDao
import com.example.parkmate.core.data.repository.ImageRepositoryImpl
import com.example.parkmate.core.data.repository.VehiclesRepositoryImpl
import com.example.parkmate.core.domain.repository.ImageRepository
import com.example.parkmate.core.domain.repository.VehiclesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VehicleModule {

    @Provides
    @Singleton
    fun provideVehicleRepository(
        dao: VehicleDao
    ): VehiclesRepository {
        return VehiclesRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context
    ): ImageRepository {
        return ImageRepositoryImpl(context)
    }
}