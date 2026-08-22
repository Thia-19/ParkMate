package com.example.parkmate.feature_home

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.feature_home.domain.MenuItem
import com.example.parkmate.feature_home.presentation.components.AppBar
import com.example.parkmate.feature_home.presentation.components.DrawHeader
import com.example.parkmate.feature_home.presentation.components.DrawerBody
import com.example.parkmate.feature_home.presentation.components.ParkingSessionCardHome
import com.example.parkmate.feature_home.presentation.components.ParkingSessionDetails
import com.example.parkmate.feature_home.util.bitmapDescriptorFromVector
import com.example.parkmate.feature_home.util.iconRes
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

/* TODO: i cazzo di bottoni di merda vengono coperti dal bagaglio*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onEvent: (HomeEvent) -> Unit = viewModel::onEvent,
    onSessionClick: (ParkingSession) -> Unit = {},
    onNavigateToAddSession: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSavedLocations: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val defaultPosition = state.sessions.firstOrNull()?.let {
        LatLng(it.position.latitude, it.position.longitude)
    } ?: LatLng(44.4939, 11.3428)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 15f)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    val scope = rememberCoroutineScope()
    var selectedSessionForDetail by rememberSaveable { mutableStateOf<ParkingSession?>(null) }
    var savedLocationsOn by rememberSaveable{ mutableStateOf(false)}

    selectedSessionForDetail?.let { session ->
        ParkingSessionDetails(
            session = session,
            onDismiss = { selectedSessionForDetail = null },
            onConcludeSession = { sessionToEnd ->
                onEvent(HomeEvent.CloseActiveSession(sessionToEnd))
                selectedSessionForDetail = null
            },
            onDeleteSession = { sessionToDelete ->
                onEvent(HomeEvent.DeleteSession(sessionToDelete))
                selectedSessionForDetail = null
            },
            onOpenMapClick = { selectedSessionForDetail = null }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                DrawHeader()
                DrawerBody(
                    items = listOf(
                        MenuItem(
                            "settings",
                            "Settings",
                            "Go to settings",
                            Icons.Default.Settings
                        ),
                        MenuItem(
                            "notifications",
                            "Notifications",
                            "Go to notifications",
                            Icons.Default.Notifications
                        ),
                        MenuItem(
                            "savedLocations",
                            "Saved Locations",
                            "Go to saved locations",
                            Icons.Default.PinDrop
                        )
                    ),
                    onItemClick = { item ->
                        scope.launch { drawerState.close() }
                        when (item.id) {
                            "settings" -> onNavigateToSettings()
                            "savedLocations" -> onNavigateToSavedLocations()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    onNavigationIconClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        ) { paddingValues ->
            BottomSheetScaffold(
                scaffoldState = bottomSheetScaffoldState,
                modifier = Modifier.padding(paddingValues),
                sheetPeekHeight = 96.dp,
                sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                sheetShadowElevation = 16.dp,
                sheetContainerColor = MaterialTheme.colorScheme.surface,
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
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Active Sessions",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Surface(
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

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 88.dp),
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
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        // contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        val context = LocalContext.current
                        val parkingIcon = remember(context) {
                            bitmapDescriptorFromVector(context, R.drawable.ic_parking_marker)
                        }

                        state.sessions.forEach { session ->
                            val position = remember(session.position.latitude, session.position.longitude) {
                                LatLng(session.position.latitude, session.position.longitude)
                            }
                            Marker(
                                state = rememberMarkerState(
                                    key = session.id.toString(),
                                    position = position
                                ),
                                snippet = "Active session",
                                icon = parkingIcon,
                                anchor = Offset(0.5f, 1.0f),
                                onClick = {
                                    selectedSessionForDetail = session
                                    true
                                }
                            )
                        }

                        if (savedLocationsOn) {
                            state.savedLocations.forEach { location ->
                                val position = remember(
                                    location.position.latitude,
                                    location.position.longitude
                                ) {
                                    LatLng(location.position.latitude, location.position.longitude)
                                }

                                val iconDescriptor = remember(location.icon, context) {
                                    bitmapDescriptorFromVector(context, location.icon.iconRes)
                                }

                                Marker(
                                    title = location.name,
                                    state = rememberMarkerState(
                                        key = location.id.toString(),
                                        position = position
                                    ),
                                    icon = iconDescriptor,
                                    anchor = Offset(
                                        0.5f,
                                        1.0f
                                    )
                                )
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = onNavigateToAddSession,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Parking Session"
                        )
                    }

                    val containerColor = if (savedLocationsOn) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh // o surface
                    }

                    val contentColor = if (savedLocationsOn) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    val icon = if (savedLocationsOn) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Outlined.BookmarkBorder
                    }

                    FloatingActionButton(
                        onClick = { savedLocationsOn = !savedLocationsOn },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd),
                        containerColor = containerColor,
                        contentColor = contentColor,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = if (savedLocationsOn) "Nascondi luoghi salvati" else "Mostra luoghi salvati"
                        )
                    }
                }
            }
        }
    }
}