package com.example.parkmate.feature_home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.feature_home.presentation.components.ParkingSessionCardHome
import com.example.parkmate.feature_home.presentation.components.ParkingSessionDetails
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onEvent: (HomeEvent) -> Unit = viewModel::onEvent,
    onSessionClick: (ParkingSession) -> Unit = {},
    onNavigateToAddSession: () -> Unit,
    onNavigateToSettings: () -> Unit,
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    var selectedSessionForDetail by rememberSaveable { mutableStateOf<ParkingSession?>(null)}

    selectedSessionForDetail?.let { session ->
        ParkingSessionDetails(
            session = session,
            onDismiss = {
                selectedSessionForDetail = null
            },
            onConcludeSession = { sessionToEnd ->
                onEvent(HomeEvent.CloseActiveSession(sessionToEnd))
                selectedSessionForDetail = null
            },
            onDeleteSession = { sessionToDelete ->
                onEvent(HomeEvent.DeleteSession(sessionToDelete))
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

    BottomSheetScaffold(
        scaffoldState = scaffoldState,

        // height
        sheetPeekHeight = 70.dp,

        // shape and shadow
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetShadowElevation = 16.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,

        // drag handle
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                height = 6.dp,
                width = 40.dp
            )
        },

        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Active Sessions",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Surface (
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${state.sessions.count()}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Parking sessions list...",
                    fontSize = 18.sp
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, start = 0.dp, end = 0.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(
                        items = state.sessions,
                        key = { _, session -> session.id }
                    ) { index, session ->
                        ParkingSessionCardHome(
                            session = session,
                            onClick = { clickedSession ->
                                selectedSessionForDetail = clickedSession
                                onSessionClick(clickedSession)
                            }
                        )

                        if (index < state.sessions.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 12.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                contentPadding = PaddingValues(top = 60.dp, bottom = 80.dp)
            ) {
            }
            FloatingActionButton (
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                onClick = onNavigateToSettings
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                onClick = { /* TODO */ },
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Layers"
                )
            }

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 80.dp, horizontal = 16.dp),
                onClick = onNavigateToAddSession,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Parking Session"
                )
            }
        }
    }
}
