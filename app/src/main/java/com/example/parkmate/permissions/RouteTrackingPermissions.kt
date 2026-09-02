package com.example.parkmate.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object RouteTrackingPermissions {

    fun isActivityRecognitionEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isNotificationEnabled(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    fun isHardwareGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.let {
            LocationManagerCompat.isLocationEnabled(it)
        } ?: false
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val hasLocationPermission = locationPermissions.any { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        val isGooglePlayServicesAvailable = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

        return hasLocationPermission && isHardwareGpsEnabled(context) && isGooglePlayServicesAvailable
    }

    fun hasAllTrackingPermissions(context: Context): Boolean {
        if (!isNotificationEnabled(context)) return false

        if (!isActivityRecognitionEnabled(context)) return false

        if (!isLocationEnabled(context)) return false

        return true
    }

    operator fun invoke(context: Context): Boolean {
        return hasAllTrackingPermissions(context)
    }

}