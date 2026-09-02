package com.example.parkmate.feature_parkingEffort.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.feature_parkingEffort.components.ParkingEffortMap
import com.example.parkmate.feature_parkingEffort.components.bottomSheet.ParkingEffortBottomSheetContent
import com.example.parkmate.feature_parkingEffort.dialog.DestinationConfirmationDialog
import com.example.parkmate.feature_parkingEffort.view_model.ParkingEffortViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingEffortMapScreen(
    viewModel: ParkingEffortViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    var selectedZone by remember { mutableStateOf<ParkingZone?>(null) }
    var clickedLocation by remember { mutableStateOf<LatLng?>(null) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 10.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        },
        sheetContent = {
            ParkingEffortBottomSheetContent(
                zone = selectedZone,
                topZones = state.topZones,
                isConnected = state.isConnected
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> LoadingContent()
                state.errorMessageResId != null -> ErrorContent(stringResource(id = state.errorMessageResId!!))
                else -> {
                    ParkingEffortMap(
                        zones = state.zones,
                        selectedZone = selectedZone,
                        onZoneSelected = { zone ->
                            selectedZone = zone
                            scope.launch {
                                if (zone != null) bottomSheetState.expand() else bottomSheetState.hide()
                            }
                        },
                        onMapClick = { latLng ->
                            clickedLocation = latLng
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    if (clickedLocation != null) {
        DestinationConfirmationDialog(
            onDismissRequest = { clickedLocation = null },
            onConfirmRequest = {
                val location = clickedLocation!!
                clickedLocation = null

                val nearest = viewModel.getNearestZone(location.latitude, location.longitude)

                if (nearest != null) {
                    selectedZone = nearest
                    scope.launch {
                        bottomSheetState.expand()
                    }
                }
            }
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}
