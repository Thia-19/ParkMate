package com.example.parkmate.service.launchers

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.parkmate.permissions.RouteTrackingPermissions

class PermissionLaunchers(
    private val locationLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    private val notificationLauncher: ManagedActivityResultLauncher<String, Boolean>,
    private val activityLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    fun requestLocationPermissions() {
        locationLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun requestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activityLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }
}

@Composable
fun rememberPermissionLaunchers(
    onPermissionsUpdated: () -> Unit
): PermissionLaunchers {
    val context = LocalContext.current

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasFine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasCoarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val isGpsEnabled = RouteTrackingPermissions.isHardwareGpsEnabled(context)

        if ((hasFine || hasCoarse) && !isGpsEnabled) {
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        onPermissionsUpdated()
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onPermissionsUpdated()
    }

    val activityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onPermissionsUpdated()
    }

    return remember(locationLauncher, notificationLauncher, activityLauncher) {
        PermissionLaunchers(
            locationLauncher = locationLauncher,
            notificationLauncher = notificationLauncher,
            activityLauncher = activityLauncher
        )
    }
}