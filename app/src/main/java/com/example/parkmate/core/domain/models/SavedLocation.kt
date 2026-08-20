package com.example.parkmate.core.domain.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MarkerIconType(val icon: ImageVector){
    HOME(Icons.Default.Home),
    JOB(Icons.Default.Work),
    SCHOOL(Icons.Default.School),
    SHOPPING_CENTRE(Icons.Default.ShoppingCart),
    BAR(Icons.Default.LocalBar),
    LIBRARY(Icons.Default.LocalLibrary),
    HOSPITAL(Icons.Default.LocalHospital),
    TRAIN_STATION(Icons.Default.Train),
    AIRPORT(Icons.Default.Flight)
}

@Entity
data class SavedLocation (
    val name: String,
    val icon: MarkerIconType,
    @Embedded val position: Position,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
){

}
