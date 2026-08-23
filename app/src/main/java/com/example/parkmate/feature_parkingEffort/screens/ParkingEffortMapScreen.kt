package com.example.parkmate.feature_parkingEffort.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.Position

@Composable
fun ParkingEffortMapScreen() {

    /* MOCK DATA */
    val mockZones = listOf(

        ParkingZone(
            centerPosition = Position(41.8902, 12.4922),
            averageScore = 80.0,
            parkingCount = 3,
            colorHex = "#4CAF50"
        ),

        ParkingZone(
            centerPosition = Position(41.8950, 12.4960),
            averageScore = 40.0,
            parkingCount = 1,
            colorHex = "#FFEB3B"
        ),

        ParkingZone(
            centerPosition = Position(41.8850, 12.4880),
            averageScore = 10.0,
            parkingCount = 5,
            colorHex = "#F44336"
        )
    )

    // Centro della fotocamera (Colosseo)
    val defaultLocation = LatLng(41.8902, 12.4922)

    val cameraPositionState = rememberCameraPositionState {

        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)
    ) {

        // Itero sulla lista per disegnare tutti i cerchi
        mockZones.forEach { zone ->
            val centerLatLng = LatLng(zone.centerPosition.latitude, zone.centerPosition.longitude)


            val composeFillColor = Color(zone.colorHex.toColorInt()).copy(alpha = 0.4f)
            val composeStrokeColor = Color(zone.colorHex.toColorInt())

            Circle(
                center = centerLatLng,
                radius = 150.0,
                fillColor = composeFillColor,
                strokeColor = composeStrokeColor,
                strokeWidth = 3f
            )
        }
    }
}