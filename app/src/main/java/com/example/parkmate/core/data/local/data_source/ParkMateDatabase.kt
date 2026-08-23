package com.example.parkmate.core.data.local.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.parkingEffort.ParkingEffort
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.models.SavedLocation

@Database(
    entities = [
        ParkingSession::class,
        Vehicle::class,
        ParkingEffort::class,
        SavedLocation::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ParkMateDatabase : RoomDatabase(){
    abstract val parkingSessionDao: ParkingSessionDao
    abstract val vehicleDao: VehicleDao
    abstract val savedLocationDao: SavedLocationDao
    abstract val parkingEffortDao: ParkingEffortDao

}

