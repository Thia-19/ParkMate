package com.example.parkmate.feature_history.presentation.parking_sessions_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.feature_history.presentation.HistoryEvent
import com.example.parkmate.feature_history.presentation.HistoryState
import com.example.parkmate.feature_history.presentation.parking_sessions_list.components.ParkingSessionCard
import com.example.parkmate.R
@Composable
fun ParkingSessionsListSection(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    ParkingSessionsList(
        sessions = state.sessions,
        onSessionClick = { item ->
            onEvent(HistoryEvent.OnSessionClick(item))
        },
        modifier = modifier
    )
}

@Composable
fun ParkingSessionsList(
    sessions: List<ParkingSessionWithVehicleInfo>,
    onSessionClick: (ParkingSessionWithVehicleInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, start = 0.dp, end = 0.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = sessions,
                    key = { item -> item.session.id }
                ) { sessionWithVehicle ->
                    ParkingSessionCard(
                        item = sessionWithVehicle,
                        onClick = onSessionClick
                    )
                }
            }
    }
}