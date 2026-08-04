package com.example.parkmate.feature_history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.feature_history.presentation.parking_sessions_list.ParkingSessionsListSection
import com.example.parkmate.feature_history.presentation.components.FilterBottomSheet
import com.example.parkmate.feature_history.presentation.parking_sessions_map.ParkingSessionsMapSection

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilterSheet by rememberSaveable { mutableStateOf(false)}

    HistoryContent(
        state = state,
        onEvent = viewModel::onEvent,
        onOpenFilterClick = {showFilterSheet = true}
    )

    if (showFilterSheet) {
        FilterBottomSheet(
            currentVehicleId = state.selectedVehicleId,
            currentParkingType = state.selectedParkingType,
            currentDateRange = state.selectedDateRange,
            onEvent = viewModel::onEvent,
            onDismissRequest = { showFilterSheet = false }
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

/*
@Preview(showBackground = true, name = "Stato con dati di esempio")
@Composable
fun HistoryContentPreview() {
    // Creiamo uno stato fittizio (Mock) con dati di prova per la Preview
    val dummyState = HistoryState(
        sessions = listOf(
            ParkingSession(
                id = 1,
                isActive = true,
                vehicleId = 101,
                parkingType = ParkingType.HOURLY_PAID,
                startTime = System.currentTimeMillis() - 7200000, // 2 ore fa
                endTime = System.currentTimeMillis(),
                position = Position(44.4949, 11.3426),
                hourlyRate = 1.50,
                finalCost = 3.00
            ),
            ParkingSession(
                id = 2,
                isActive = false,
                vehicleId = 102,
                parkingType = ParkingType.FIXED_TICKET,
                startTime = System.currentTimeMillis() - 86400000,
                endTime = System.currentTimeMillis() - 80000000,
                position = Position(44.5000, 11.3500),
                fixedCost = 5.00,
                finalCost = 5.00
            )
        ),
        isLoading = false
    )

    // Passiamo lo stato fittizio e una lambda vuota per gli eventi
    HistoryContent(
        state = dummyState,
        onEvent = {}
    )
}
*/
