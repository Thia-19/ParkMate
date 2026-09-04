package com.example.parkmate.di

import android.content.Context
import android.hardware.SensorManager
import androidx.room.Room
import androidx.work.WorkManager
import com.example.parkmate.core.data.local.data_source.ParkMateDatabase
import com.example.parkmate.core.data.local.data_source.ParkingEffortDao
import com.example.parkmate.core.data.local.data_source.ParkingSessionDao
import com.example.parkmate.core.data.local.data_source.SavedLocationDao
import com.example.parkmate.core.data.local.data_source.VehicleDao
import com.example.parkmate.core.data.repository.ImageRepositoryImpl
import com.example.parkmate.core.data.repository.ParkingEffortRepositoryImpl
import com.example.parkmate.core.domain.repository.SavedLocationRepository
import com.example.parkmate.core.data.repository.SavedLocationsRepositoryImpl
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.core.data.repository.ParkingSessionsRepositoryImpl
import com.example.parkmate.core.data.repository.VehiclesRepositoryImpl
import com.example.parkmate.core.domain.repository.ImageRepository
import com.example.parkmate.core.domain.repository.ParkingEffortRepository
import com.example.parkmate.core.domain.repository.VehiclesRepository
import com.example.parkmate.core.domain.util.observers.ConnectivityObserver
import com.example.parkmate.core.domain.util.observers.NetworkConnectivityObserver
import com.example.parkmate.debug.AppDatabaseCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideParkMateDatabase(
        @ApplicationContext context: Context
    ): ParkMateDatabase {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        lateinit var database: ParkMateDatabase

        database = Room.databaseBuilder(
            context,
            ParkMateDatabase::class.java,
            "parkmate_db"
        )
            .addCallback(
                AppDatabaseCallback(
                    scope = scope,
                    provider = { database }
                )
            )
            .build()

        return database
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

    @Provides
    @Singleton
    fun provideParkingEffortRepository(
        dao: ParkingEffortDao
    ): ParkingEffortRepository {
        return ParkingEffortRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSensorManager(
        @ApplicationContext context: Context
    ): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(
        @ApplicationContext context: Context
    ): ConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }

}
