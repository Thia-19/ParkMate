package com.example.parkmate.core.data.local.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertVehicle(vehicle: Vehicle) : Long
    @Query("SELECT * FROM vehicles ORDER BY name ASC")
    fun getVehicles(): Flow<List<Vehicle>>
    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getVehicleById(id: Int): Flow<Vehicle?>
    @Update
    suspend fun updateVehicle(vehicle: Vehicle)
    @Delete
    suspend fun deleteVehicle(vehicle: Vehicle)

}