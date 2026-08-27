package com.example.parkmate.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object RouteTrackingPermissions {

    fun hasRequiredPermissions(context: Context): Boolean {
        val hasLocation = hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        val hasActivityRecognition = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                hasPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
        val hasNotificationPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                hasPermission(context, Manifest.permission.POST_NOTIFICATIONS)

        return hasLocation && hasActivityRecognition && hasNotificationPermission
    }

    fun permissionsToRequest(context: Context): Array<String> = buildList {
        if (!hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
            !hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !hasPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
        ) {
            add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !hasPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        ) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    private fun hasPermission(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}