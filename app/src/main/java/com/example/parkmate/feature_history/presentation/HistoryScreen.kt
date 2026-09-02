package com.example.parkmate.feature_history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
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
    var selectedSessionForDetail by rememberSaveable { mutableStateOf<ParkingSessionWithVehicleInfo?>(null)}

    HistoryContent(
        state = state,
        onEvent = { event ->
            if (event is HistoryEvent.OnSessionClick) {
                selectedSessionForDetail = event.item
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
            item = session,
            onDismiss = {
                selectedSessionForDetail = null
            },
            onConcludeSession = { // NULL
            },
            onDeleteSession = { sessionToDelete ->
                onEvent(HistoryEvent.DeleteSession(sessionToDelete))
                selectedSessionForDetail = null
            },
            onOpenMapClick = { // NULL
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
                     if(state.allSessions.isEmpty()){
                        NoConcludedParkingSessions()
                     } else {
                         ParkingSessionsListSection(
                             state = state,
                             onEvent = onEvent
                         )
                     }
                }

                HistoryTab.ANALYTICS -> {
                    ParkingSessionsAnalytics(
                        state = state,
                        vehicles = state.userVehicles
                    )
                }

                 HistoryTab.MAP -> {
                     if(state.allSessions.isEmpty()){
                         NoConcludedParkingSessions()
                     } else {
                         ParkingSessionsMapSection(
                             state = state,
                         )
                     }
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

@Composable
private fun NoConcludedParkingSessions(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            )
            .background(MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f))
    ) {
        Column {
            Text(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp
                ),
                text = stringResource(R.string.history_sessions_month_alt),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant

            )

            HorizontalDivider()

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                modifier = Modifier.padding(
                    16.dp
                ),
                text = stringResource(R.string.history_alt),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
