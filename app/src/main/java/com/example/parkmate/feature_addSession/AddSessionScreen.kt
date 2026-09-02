package com.example.parkmate.feature_addSession

import android.Manifest
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.labelRes
import com.example.parkmate.core.domain.utils.images.createImageUri
import com.example.parkmate.feature_addSession.components.ParkingDateTimePickerDialog
import com.example.parkmate.feature_addSession.components.SavedLocationsDropdownSelector
import com.example.parkmate.feature_addSession.components.VehicleDropdownSelector
import com.example.parkmate.feature_addSession.components.formatTimestampToTime
import com.example.parkmate.feature_parkingEffort.dialog.RouteTrackingEntryDialog
import com.example.parkmate.notifications.NotificationsHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddSessionScreen(
    viewModel: AddSessionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var tempPicUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPicUri != null){
            viewModel.onEvent(AddSessionEvent.OnPhotoCaptured(tempPicUri.toString()))
        }
    }

    val onTakePhoto: () -> Unit = {
        val uri = createImageUri(context)
        tempPicUri = uri
        cameraLauncher.launch(uri)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if(!isGranted){
            Toast.makeText(
                context,
                context.getString((R.string.add_session_toast)),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            viewModel.onEvent(AddSessionEvent.FetchLocationRequested)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotificationPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasNotificationPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val hasFineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.onEvent(AddSessionEvent.FetchLocationRequested)
        }
    }

    AddSessionContent(
        state = state,
        onEvent = viewModel::onEvent,
        onTakePhoto = onTakePhoto,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSessionContent (
    state: AddSessionState,
    onEvent: (AddSessionEvent) -> Unit,
    onTakePhoto: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showTimePicker by remember { mutableStateOf(false) }

    val defaultPosition = LatLng(44.4939, 11.3428)

    val currentLatLng = remember(state.position) {
        state.position?.let { LatLng(it.latitude, it.longitude) } ?: defaultPosition
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
    }

    LaunchedEffect(currentLatLng) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
        )
    }

    if (showTimePicker) {
        ParkingDateTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onDateTimeSelected = { timestamp ->
                showTimePicker = false
                onEvent(AddSessionEvent.OnExpiryTimeChanged(timestamp))
            }
        )
    }

    if (state.isSaved) {
        state.savedSessionId?.let { id ->
            RouteTrackingEntryDialog(
                sessionId = id,
                onDismissRequest = {onNavigateBack()}
            )
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_session_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onEvent(AddSessionEvent.OnSaveSession) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.add_session_save), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.add_session_vehicle),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            VehicleDropdownSelector(
                vehicles = state.vehicles,
                selectedVehicleId = state.vehicleId,
                onVehicleSelected = { vehicleId ->
                    onEvent(AddSessionEvent.OnVehicleSelected(vehicleId))
                }
            )

            HorizontalDivider()

            Text(
                text = stringResource(R.string.add_session_position),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            SavedLocationsDropdownSelector(
                locations = state.locations,
                selectedLocationId = state.locationId,
                onLocationSelected = { locationId ->
                    if (locationId == null) {
                        onEvent(AddSessionEvent.FetchLocationRequested)
                    } else {
                        onEvent(AddSessionEvent.OnSavedLocationSelected(locationId))
                    }
                }
            )

            Text(
                text = stringResource(R.string.add_session_adjust_position),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    onEvent(
                        AddSessionEvent.OnCustomPositionSelected(
                            Position(latLng.latitude, latLng.longitude)
                        )
                    )
                }
            ) {
                Marker(
                    state = MarkerState(position = currentLatLng),
                    title = "Parking Location"
                )
            }

            Text(
                text = stringResource(R.string.add_session_position_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Text(
                text = stringResource(R.string.add_session_parking_type),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            ParkingTypeSelector(
                selectedType = state.parkingType,
                onTypeSelected = { type ->
                    onEvent(AddSessionEvent.OnParkingTypeChanged(type))
                }
            )

            HorizontalDivider()
            when (state.parkingType) {
                ParkingType.HOURLY_PAID -> {
                    OutlinedTextField(
                        value = state.hourlyRate?.toString() ?: "",
                        onValueChange = { value ->
                            val rate = value.toDoubleOrNull()
                            onEvent(AddSessionEvent.OnHourlyRateChanged(rate))
                        },
                        label = { Text(stringResource(R.string.add_session_hourly_rate)) },
                        leadingIcon = { Icon(Icons.Default.Euro, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ParkingType.FIXED_TICKET -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.fixedCost?.toString() ?: "",
                            onValueChange = { value ->
                                val cost = value.toDoubleOrNull()
                                onEvent(AddSessionEvent.OnFixedCostChanged(cost))
                            },
                            label = { Text(stringResource(R.string.add_session_fixed)) },
                            leadingIcon = { Icon(Icons.Default.Euro, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formatTimestampToTime(state.expiryTime),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.add_session_expiry_time)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showTimePicker = true }) {
                                        Icon(
                                            imageVector = Icons.Default.EditCalendar,
                                            contentDescription = "Seleziona orario"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showTimePicker = true }
                            )
                        }
                    }
                }

                ParkingType.FREE -> {
                    Text(
                        text = stringResource(R.string.add_session_free),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = stringResource(R.string.add_session_additional_info),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (state.photoUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = state.photoUri,
                        contentDescription = "Parking picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onEvent(AddSessionEvent.OnPhotoRemoved) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove pic",
                            tint = Color.White
                        )
                    }
                }
            } else {
                OutlinedButton (
                    onClick = onTakePhoto,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_session_take_pic))
                }
            }

            OutlinedTextField(
                value = state.note,
                onValueChange = { newNote ->
                    onEvent(AddSessionEvent.OnNoteChanged(newNote))
                },
                label = { Text(stringResource(R.string.add_session_note)) },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            Text(
                text = stringResource(R.string.add_session_notifications),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.add_session_notification),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.add_session_notification_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = state.isNotificationEnabled,
                        onCheckedChange = { isChecked ->
                            onEvent(AddSessionEvent.OnNotificationToggleChanged(isChecked))
                        }
                    )
                }
            }

            if (state.parkingType == ParkingType.FIXED_TICKET) {
                showExpiryCard(
                    isEnabled = state.isNotificationExpiryEnabled,
                    onToggleEnable = { event -> onEvent(event) }
                )
            }
        }
    }
}

@Composable
private fun showExpiryCard (
    isEnabled: Boolean,
    onToggleEnable: (AddSessionEvent) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.add_session_notification_expiry),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.add_session_notification_expiry_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { isChecked ->
                    onToggleEnable(AddSessionEvent.OnExpiryNotificationToggleChanged(isChecked))
                }
            )
        }
    }
}


@Composable
private fun ParkingTypeSelector (
    selectedType: ParkingType,
    onTypeSelected: (ParkingType) -> Unit
){
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        ParkingType.entries.forEachIndexed { index, type ->
            SegmentedButton(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = ParkingType.entries.size)
            ) {
                Text(
                    text = stringResource(id = type.labelRes)
                )
            }
        }
    }
}