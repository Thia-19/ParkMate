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

    // Usiamo 'suspend' invece di 'async'
    private suspend fun populateMockData(database: ParkMateDatabase) {
        // Inseriamo i veicoli uno alla volta usando la insert singola del tuo DAO
        MockData.vehicles.forEach { vehicle ->
            database.vehicleDao.insertVehicle(vehicle) // Sostituisci '.insert' col nome esatto del tuo metodo
        }

        // Inseriamo le sessioni una alla volta
        MockData.mockParkingSessions.forEach { session ->
            database.parkingSessionDao.insertParkingSession(session) // Sostituisci '.insert' col nome esatto del tuo metodo
        }
    }
}