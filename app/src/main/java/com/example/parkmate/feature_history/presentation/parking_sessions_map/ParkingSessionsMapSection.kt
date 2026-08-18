package com.example.parkmate.feature_history.presentation.parking_sessions_map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.parkmate.feature_history.presentation.HistoryEvent
import com.example.parkmate.feature_history.presentation.HistoryState
import com.google.maps.android.compose.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberTileOverlayState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import android.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.parkmate.feature_history.domain.models.TimeRange
import com.example.parkmate.feature_history.presentation.parking_sessions_map.components.TimeRangeSelector
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingSessionsMapSection(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier
){
    var selectedTimeRange by remember { mutableStateOf(TimeRange.ALL_TIME) }
    val filteredPoints = remember (state.allSessions, selectedTimeRange){
        val startTimestamp = selectedTimeRange.getStartTimestamp()

        state.allSessions
            .filter { session ->
                startTimestamp == null || session.startTime >= startTimestamp
            }
            .map { session ->
                LatLng(session.position.latitude, session.position.longitude)
            }
    }

    val colors = intArrayOf(
        Color.rgb(102, 225, 0),
        Color.rgb(255, 0, 0)
    )

    val startPoints = floatArrayOf(0.2f, 1.0f)
    val gradient = Gradient(colors, startPoints)

    val tileProvider = remember(filteredPoints) {
        if (filteredPoints.isNotEmpty()) {
            HeatmapTileProvider.Builder()
                .data(filteredPoints)
                .gradient(gradient)
                .opacity(0.7)
                .radius(30)
                .build()
        } else {
            null
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            contentPadding = PaddingValues(top = 60.dp)
        ) {
            if (tileProvider != null) {
                TileOverlay(
                    tileProvider = tileProvider,
                    state = rememberTileOverlayState()
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            TimeRangeSelector(
                selectedRange = selectedTimeRange,
                onRangeSelected = { newRange ->
                    selectedTimeRange = newRange
                }
            )

            // no parkings
            if (filteredPoints.isEmpty()) {
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text = "No parking session found for this period.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

}