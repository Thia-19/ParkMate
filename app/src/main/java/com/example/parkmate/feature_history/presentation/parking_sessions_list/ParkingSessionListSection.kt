package com.example.parkmate.feature_history.presentation.parking_sessions_list

import com.example.parkmate.core.domain.models.ParkingSession
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmate.feature_history.presentation.HistoryEvent
import com.example.parkmate.feature_history.presentation.HistoryState
import com.example.parkmate.feature_history.presentation.parking_sessions_list.components.ParkingSessionCard

@Composable
fun ParkingSessionsListSection(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    ParkingSessionsList(
        sessions = state.sessions,
        onSessionClick = { session ->
            onEvent(HistoryEvent.OnSessionClick(session))
        },
        modifier = modifier
    )
}

// 2. LISTA STATELESS
@Composable
fun ParkingSessionsList(
    sessions: List<ParkingSession>,
    onSessionClick: (ParkingSession) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (sessions.isEmpty()) {
            Text(
                text = "Nessuna sessione di parcheggio trovata",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, start = 0.dp, end = 0.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = sessions,
                    key = { session -> session.id }
                ) { session ->
                    ParkingSessionCard(
                        session = session,
                        onClick = onSessionClick
                    )
                }
            }
        }
    }
}