package com.example.parkmate.core.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vehicle (
    val name: String,
    val licensePlate: String?,
    @PrimaryKey val id: Int
){

}