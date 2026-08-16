package com.example.parkmate.core.domain.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SavedLocation (
    val name: String,
    @Embedded val position: Position,
    @PrimaryKey val id: Int
){

}
