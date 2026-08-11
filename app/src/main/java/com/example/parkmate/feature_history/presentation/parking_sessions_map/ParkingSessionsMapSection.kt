package com.example.parkmate.feature_history.presentation.parking_sessions_map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parkmate.feature_history.presentation.HistoryEvent
import com.example.parkmate.feature_history.presentation.HistoryState
import com.google.maps.android.compose.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingSessionsMapSection(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier
){
    val colosseo = LatLng(41.8902, 12.4922)

    // 2. Creiamo lo stato della fotocamera con un livello di zoom (15f = livello strada)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(colosseo, 15f)
    }

    // 3. Mostriamo la mappa a schermo intero
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = colosseo),
            title = "Colosseo",
            snippet = "Se vedi la mappa, l'API Key funziona!"
        )
    }
}