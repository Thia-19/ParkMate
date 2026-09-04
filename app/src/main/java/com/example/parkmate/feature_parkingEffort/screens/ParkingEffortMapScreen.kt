package com.example.parkmate.feature_parkingEffort.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.feature_parkingEffort.components.ParkingEffortMap
import com.example.parkmate.feature_parkingEffort.components.bottomSheet.ParkingEffortBottomSheetContent
import com.example.parkmate.feature_parkingEffort.dialog.DestinationConfirmationDialog
import com.example.parkmate.feature_parkingEffort.dialog.ParkingEffortHelpDialog
import com.example.parkmate.feature_parkingEffort.view_model.ParkingEffortEvent
import com.example.parkmate.feature_parkingEffort.view_model.ParkingEffortUiState
import com.example.parkmate.feature_parkingEffort.view_model.ParkingEffortViewModel
import com.example.parkmate.notifications.NotificationsHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingEffortMapScreen(
    viewModel: ParkingEffortViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val operationFailedMessage = stringResource(id = R.string.operation_failed)

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    var selectedZone by remember { mutableStateOf<ParkingZone?>(null) }
    var clickedLocation by remember { mutableStateOf<LatLng?>(null) }
    var showHelpDialog by remember { mutableStateOf(false) }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is ParkingEffortEvent.OperationFailed ->
                        NotificationsHelper.showErrorNotification(context, operationFailedMessage)
                }
            }
        }
    }

    val successState = state as? ParkingEffortUiState.Success

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 136.dp,
        sheetShape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        ),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetTonalElevation = 8.dp,
        sheetShadowElevation = 8.dp,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        },
        sheetContent = {
            ParkingEffortBottomSheetContent(
                zone = selectedZone,
                topZones = successState?.topZones.orEmpty(),
                isConnected = successState?.isConnected ?: true
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                ParkingEffortUiState.Loading -> LoadingContent()

                is ParkingEffortUiState.Error -> ErrorContent(
                    message = stringResource(id = currentState.messageResId)
                )

                is ParkingEffortUiState.Success -> {
                    ParkingEffortMap(
                        zones = currentState.zones,
                        selectedZone = selectedZone,
                        onZoneSelected = { zone ->
                            selectedZone = zone
                            scope.launch {
                                if (zone != null) {
                                    bottomSheetState.expand()
                                } else {
                                    bottomSheetState.partialExpand()
                                }
                            }
                        },
                        onMapClick = { latLng -> clickedLocation = latLng },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                IconButton(onClick = { showHelpDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = stringResource(R.string.pe_help_dialog_title),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    if (showHelpDialog) {
        ParkingEffortHelpDialog(onDismissRequest = { showHelpDialog = false })
    }

    clickedLocation?.let { location ->
        DestinationConfirmationDialog(
            onDismissRequest = { clickedLocation = null },
            onConfirmRequest = {
                clickedLocation = null
                val nearest = viewModel.getNearestZone(location.latitude, location.longitude)
                if (nearest != null) {
                    selectedZone = nearest
                    scope.launch { bottomSheetState.expand() }
                }
            }
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}