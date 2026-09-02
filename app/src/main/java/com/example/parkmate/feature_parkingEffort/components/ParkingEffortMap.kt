package com.example.parkmate.feature_parkingEffort.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.util.map.getConvenienceColorHex
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private const val ZONE_RADIUS_METERS = 150.0
private val DEFAULT_LOCATION = LatLng(41.8902, 12.4922)

@Composable
fun ParkingEffortMap(
    zones: List<ParkingZone>,
    selectedZone: ParkingZone?,
    onZoneSelected: (ParkingZone?) -> Unit,
    onMapClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialPosition = remember(zones) {
        zones.firstOrNull()?.let {
            LatLng(it.centerPosition.latitude, it.centerPosition.longitude)
        } ?: DEFAULT_LOCATION
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 14f)
    }

    LaunchedEffect(zones) {
        if (zones.isNotEmpty() && selectedZone == null) {
            val firstZone = zones.first()
            val targetPosition = LatLng(firstZone.centerPosition.latitude, firstZone.centerPosition.longitude)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(targetPosition, 14f)
            )
        }
    }

    LaunchedEffect(selectedZone) {
        selectedZone?.let { zone ->
            val position = LatLng(zone.centerPosition.latitude, zone.centerPosition.longitude)
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(position, 15f))
        }
    }

    val cdParkingZone = stringResource(id = R.string.pe_map_cd_parking_zone)

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        onMapClick = { latLng ->
            onZoneSelected(null)
            onMapClick(latLng)
        }
    ) {
        zones.forEach { zone ->
            val color = Color(getConvenienceColorHex(zone.averageScore).toColorInt())
            val isSelected = selectedZone?.id == zone.id

            Circle(
                center = LatLng(zone.centerPosition.latitude, zone.centerPosition.longitude),
                radius = ZONE_RADIUS_METERS,
                fillColor = color.copy(alpha = if (isSelected) 0.55f else 0.35f),
                strokeColor = color,
                strokeWidth = if (isSelected) 6f else 3f,
                clickable = true,
                onClick = { onZoneSelected(zone) }
            )

            MarkerComposable(
                keys = arrayOf(zone.id, color),
                state = rememberMarkerState(
                    key = "center_${zone.id}",
                    position = LatLng(zone.centerPosition.latitude, zone.centerPosition.longitude)
                ),
                onClick = {
                    onZoneSelected(zone)
                    true
                }
            ) {
                Surface(
                    shape = CircleShape,
                    color = color,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .size(36.dp)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalParking,
                            contentDescription = cdParkingZone,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        selectedZone?.recentDestinations?.forEachIndexed { index, destPos ->
            Marker(
                state = rememberMarkerState(
                    key = "dest_${selectedZone.id}_$index",
                    position = LatLng(destPos.latitude, destPos.longitude)
                ),
                title = stringResource(id = R.string.pe_map_destination_marker_title, index + 1),
                snippet = stringResource(id = R.string.pe_map_destination_marker_snippet)
            )
        }
    }
}