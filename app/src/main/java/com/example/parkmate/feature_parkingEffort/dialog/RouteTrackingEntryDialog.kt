package com.example.parkmate.feature_parkingEffort.dialog

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkmate.R
import com.example.parkmate.feature_parkingEffort.view_model.RouteTrackingEvent
import com.example.parkmate.feature_parkingEffort.view_model.RouteTrackingViewModel
import com.example.parkmate.notifications.NotificationsHelper
import com.example.parkmate.permissions.RouteTrackingPermissions
import com.example.parkmate.service.launchers.rememberPermissionLaunchers

/**
 * FUNZIONALITA' :
 * - Richiesta e verifica dinamica dei permessi
 * - Richiamo dei launcher Android
 * - Avvio tracciamento percorso per valutazione Parking Effort
 */

@Composable
fun RouteTrackingEntryDialog(
    sessionId: Int,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RouteTrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var isLocationGranted by remember { mutableStateOf(RouteTrackingPermissions.isLocationEnabled(context)) }
    var isActivityGranted by remember { mutableStateOf(RouteTrackingPermissions.isActivityRecognitionEnabled(context)) }
    var isNotificationGranted by remember { mutableStateOf(RouteTrackingPermissions.isNotificationEnabled(context)) }

    val refreshPermissionsState = {
        isLocationGranted = RouteTrackingPermissions.isLocationEnabled(context)
        isActivityGranted = RouteTrackingPermissions.isActivityRecognitionEnabled(context)
        isNotificationGranted = RouteTrackingPermissions.isNotificationEnabled(context)
    }

    val permissionLaunchers = rememberPermissionLaunchers(
        onPermissionsUpdated = {
            refreshPermissionsState()
            Log.i("Attivita dei lauchers", "Stato permessi aggiornato")
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshPermissionsState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { state ->
                when (state) {
                    RouteTrackingEvent.Started -> onDismissRequest()
                    is RouteTrackingEvent.FailedToStart -> {
                        val resId = if (state.error is SecurityException) {
                            R.string.permissions_denied_system
                        } else {
                            R.string.srcd_route_tracking_start_failed
                        }
                        NotificationsHelper.showErrorNotification(context, context.getString(resId))
                    }
                }
            }
        }
    }

    val isAllPermissionsGranted =
            isLocationGranted
            && isActivityGranted
            && isNotificationGranted

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = AlertDialogDefaults.shape,
            colors = CardDefaults.cardColors(
                containerColor = AlertDialogDefaults.containerColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = AlertDialogDefaults.TonalElevation
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.srcd_rate_parking_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiusura del Dialog"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.srcd_enable_permissions_info),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                PermissionSwitchRow(
                    title = stringResource(R.string.srcd_permission_location),
                    isGranted = isLocationGranted,
                    onRequest = { permissionLaunchers.requestLocationPermissions() }
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    PermissionSwitchRow(
                        title = stringResource(R.string.srcd_permission_activity_tracking),
                        isGranted = isActivityGranted,
                        onRequest = { permissionLaunchers.requestActivityRecognitionPermission() }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.grant_ActRecognition_permission_toContinue),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionSwitchRow(
                        title = stringResource(R.string.srcd_permission_notifications),
                        isGranted = isNotificationGranted,
                        onRequest = { permissionLaunchers.requestNotificationPermission() }
                    )
                } else {
                    Text(
                        text = stringResource(R.string.grant_notifications_permission_toContinue),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.startRouteTracking(sessionId)
                    },
                    enabled = isAllPermissionsGranted,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.srcd_action_start_tracking))
                }
            }
        }
    }
}


@Composable
private fun PermissionSwitchRow(
    title: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isGranted,
            onCheckedChange = { isChecking ->
                if (isChecking && !isGranted) {
                    onRequest()
                } else if (!isChecking && isGranted) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.srcd_cannot_revoke_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}