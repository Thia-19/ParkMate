package com.example.parkmate.di

import android.content.Context
import androidx.room.Room
import com.example.parkmate.core.data.local.data_source.ParkMateDatabase
import com.example.parkmate.core.data.local.data_source.ParkingEffortDao
import com.example.parkmate.core.data.local.data_source.ParkingSessionDao
import com.example.parkmate.core.data.local.data_source.SavedLocationDao
import com.example.parkmate.core.data.local.data_source.VehicleDao
import com.example.parkmate.core.domain.repository.SavedLocationRepository
import com.example.parkmate.core.data.repository.SavedLocationsRepositoryImpl
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.core.data.repository.ParkingSessionsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideParkMateDatabase(
        @ApplicationContext context: Context
    ): ParkMateDatabase {
        return Room.databaseBuilder (
            context,
            ParkMateDatabase::class.java,
            "parkmate_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideParkingSessionDao(db: ParkMateDatabase): ParkingSessionDao {
        return db.parkingSessionDao
    }

    @Provides
    @Singleton
    fun provideParkingSessionRepository(
        dao: ParkingSessionDao
    ): ParkingSessionRepository{
        return ParkingSessionsRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideVehicleDao(db: ParkMateDatabase): VehicleDao {
        return db.vehicleDao
    }

    @Provides
    @Singleton
    fun provideSavedLocationDao(db: ParkMateDatabase): SavedLocationDao {
        return db.savedLocationDao
    }

    @Provides
    @Singleton
    fun provideSavedLocationRepository(
        dao: SavedLocationDao
    ): SavedLocationRepository {
        return SavedLocationsRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideParkingEffortDao(db: ParkMateDatabase): ParkingEffortDao {
        return db.parkingEffortDao
    }

}
