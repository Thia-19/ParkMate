package com.example.parkmate.feature_parkingEffort.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.parkmate.R
import com.example.parkmate.permissions.rememberRouteTrackingPermissionRequester
import com.example.parkmate.feature_parkingEffort.view_model.RouteTrackingEvent
import com.example.parkmate.feature_parkingEffort.view_model.RouteTrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartRouteCalculationDialog(
    sessionId: Int,
    onDismissRequest: () -> Unit,
    onTrackingStarted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RouteTrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val requestPermissions = rememberRouteTrackingPermissionRequester(
        onGranted = { viewModel.startRouteTracking(sessionId) },
        onDenied = { errorMessage = context.getString(R.string.route_tracking_permissions_required) }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RouteTrackingEvent.Started -> {
                    onTrackingStarted()
                }
                is RouteTrackingEvent.StartFailed -> {
                    event.error?.printStackTrace()

                    errorMessage = context.getString(R.string.route_tracking_start_failed)

                    if (event.error is SecurityException) {
                    errorMessage = "Permessi negati dal sistema."
                     }
                }
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = DialogProperties()
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Icon(
                    imageVector = Icons.Outlined.DirectionsWalk,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.start_route_calculation_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.start_route_calculation_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(message, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.start_route_calculation_no))
                    }
                    Button(onClick = requestPermissions) {
                        Text(stringResource(R.string.start_route_calculation_yes))
                    }
                }
            }
        }
    }
}