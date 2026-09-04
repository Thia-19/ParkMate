package com.example.parkmate.core.domain.models.vehicle

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class VehicleType {
    MOTORCYCLE,
    CAR,
    BICYCLE,
    OTHER
}

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: VehicleType,
    val imageUri: String? = null
)
