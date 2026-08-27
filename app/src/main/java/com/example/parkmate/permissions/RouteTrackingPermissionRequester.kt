package com.example.parkmate.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberRouteTrackingPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (RouteTrackingPermissions.hasRequiredPermissions(context)) onGranted() else onDenied()
    }

    return remember(context, permissionLauncher, onGranted, onDenied) {
        {
            if (RouteTrackingPermissions.hasRequiredPermissions(context)) {
                onGranted()
            } else {
                permissionLauncher.launch(RouteTrackingPermissions.permissionsToRequest(context))
            }
        }
    }
}