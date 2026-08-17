package com.example.parkmate.feature_history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.parkmate.feature_history.presentation.parking_sessions_list.ParkingSessionsListSection
import com.example.parkmate.feature_history.presentation.components.FilterBottomSheet
import com.example.parkmate.feature_history.presentation.components.HistoryTopTabBar
import com.example.parkmate.feature_history.presentation.parking_sessions_analytics.ParkingSessionsAnalytics
import com.example.parkmate.feature_history.presentation.parking_sessions_map.ParkingSessionsMapSection
import com.example.parkmate.feature_home.presentation.components.ParkingSessionDetails

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onEvent: (HistoryEvent) -> Unit = viewModel::onEvent,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilterSheet by rememberSaveable { mutableStateOf(false)}
    var selectedSessionForDetail by rememberSaveable { mutableStateOf<ParkingSession?>(null)}

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
        ParkingSessionDetails(
            session = session,
            onDismiss = {
                selectedSessionForDetail = null
            },
            onConcludeSession = { // NULL
            },
            onDeleteSession = { sessionToDelete ->
                onEvent(HistoryEvent.DeleteSession(sessionToDelete))
                selectedSessionForDetail = null
            },
            onOpenMapClick = {
                /* position ->
                val mapUri = Uri.parse("geo:${position.latitude},${position.longitude}?q=${position.latitude},${position.longitude}(Parcheggio)")
                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                context.startActivity(mapIntent)

                 */
            }
        )
    }

}

@Composable
fun HistoryContent(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    onOpenFilterClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        HistoryTopTabBar(
            selectedTab = state.selectedTab,
            onTabSelected = { newTab ->
                onEvent(HistoryEvent.OnTabSelected(newTab))
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (state.selectedTab) {
                 HistoryTab.LIST -> {
                    ParkingSessionsListSection(
                        state = state,
                        onEvent = onEvent
                    )
                }

                HistoryTab.ANALYTICS -> {
                    ParkingSessionsAnalytics(
                        state = state,
                        onEvent = onEvent,
                        vehicles = state.userVehicles
                    )
                }

                 HistoryTab.MAP -> {
                    ParkingSessionsMapSection(
                        state = state,
                        onEvent = onEvent
                    )
                }

            }
            if(state.selectedTab == HistoryTab.LIST) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
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
}

