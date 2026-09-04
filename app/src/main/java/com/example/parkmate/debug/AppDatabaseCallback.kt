package com.example.parkmate.debug

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.parkmate.core.data.local.data_source.ParkMateDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.parkmate.BuildConfig

class AppDatabaseCallback(
    private val scope: CoroutineScope,
    private val provider: () -> ParkMateDatabase
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        if (BuildConfig.ENABLE_MOCK_DATA) {
            scope.launch(Dispatchers.IO) {
                populateMockData(provider())
            }
        }
    }
    private suspend fun populateMockData(database: ParkMateDatabase) {
        MockData.vehicles.forEach { vehicle ->
            database.vehicleDao.insertVehicle(vehicle)
        }

        MockData.mockParkingSessions.forEach { session ->
            database.parkingSessionDao.insertParkingSession(session)
        }
    }
}