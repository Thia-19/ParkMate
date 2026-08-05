package com.example.parkmate.feature_history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.Vehicle
import com.example.parkmate.feature_history.presentation.parking_sessions_list.ParkingSessionsListSection
import com.example.parkmate.feature_history.presentation.components.FilterBottomSheet
import com.example.parkmate.feature_history.presentation.parking_sessions_list.components.ConfirmDeleteDialog
import com.example.parkmate.feature_history.presentation.parking_sessions_list.components.ParkingSessionDetailBottomSheet
import com.example.parkmate.feature_history.presentation.parking_sessions_map.ParkingSessionsMapSection

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilterSheet by rememberSaveable { mutableStateOf(false)}
    var selectedSessionForDetail by rememberSaveable { mutableStateOf<ParkingSession?>(null)}
    var sessionToDelete by remember { mutableStateOf<ParkingSession?>(null) }

    HistoryContent(
        state = state,
        onEvent = { event ->
            if (event is HistoryEvent.OnSessionClick) {
                selectedSessionForDetail = event.session
            }
            viewModel.onEvent(event)
        },
        onOpenFilterClick = { showFilterSheet = true }
    )

    if (showFilterSheet) {
        FilterBottomSheet(
            vehicles = state.userVehicles,
            currentVehicleId = state.selectedVehicleId,
            currentParkingType = state.selectedParkingType,
            startDate = state.selectedStartDate,
            endDate = state.selectedEndDate,
            onEvent = viewModel::onEvent,
            onDismissRequest = { showFilterSheet = false }
        )
    }

    selectedSessionForDetail?.let { session ->
        ParkingSessionDetailBottomSheet(
            session = session,
            onDismissRequest = { selectedSessionForDetail = null },
            onDeleteClick = { sessionToConfirm ->
                sessionToDelete = sessionToConfirm
            }
        )
    }

    sessionToDelete?.let { session ->
        ConfirmDeleteDialog(
            onConfirm = {
                viewModel.onEvent(HistoryEvent.DeleteSession(session))
                sessionToDelete = null
                selectedSessionForDetail = null
            },
            onDismiss = {
                sessionToDelete = null
            }
        )
    }

}

// 2. Componente di sola UI (Stateless)
@Composable
fun HistoryContent(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    onOpenFilterClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // CONTENUTO PRINCIPALE (Lista o Mappa)
        when (state.viewMode) {
            HistoryViewMode.LIST -> {
                ParkingSessionsListSection(
                    state = state,
                    onEvent = onEvent
                )
            }
            HistoryViewMode.MAP -> {
                ParkingSessionsMapSection(
                    state = state,
                    onEvent = onEvent
                )
            }
        }

        // FLOATING ACTION BUTTONS (Posizionati in basso a destra sopra il contenuto)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp), // Distanza dai bordi e dalla BottomBar
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // FAB per vedere i grafici
            FloatingActionButton(
                onClick = { /*TODO */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Analytics"
                )
            }

            // FAB per cambiare vista
            FloatingActionButton(
                onClick = { onEvent(HistoryEvent.ChangeViewMode) }
            ) {
                Icon(
                    imageVector = if (state.viewMode == HistoryViewMode.LIST) Icons.Default.Place else Icons.Default.Menu,
                    contentDescription = if (state.viewMode == HistoryViewMode.LIST) "Go to Map View" else "Go to List View"
                )
            }

            // FAB per i filtri
            FloatingActionButton(
                onClick = onOpenFilterClick
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter Sessions"
                )
            }
        }
    }
}

