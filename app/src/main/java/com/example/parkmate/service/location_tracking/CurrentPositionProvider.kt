package com.example.parkmate.service.location_tracking

import android.annotation.SuppressLint
import android.content.Context
import com.example.parkmate.permissions.RouteTrackingPermissions
import com.example.parkmate.core.domain.models.Position
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPositionProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentPosition(): Position? {
        if (!RouteTrackingPermissions.hasRequiredPermissions(context)) return null

        return try {
            val location = locationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await() ?: locationClient.lastLocation.await()

            location?.let { Position(it.latitude, it.longitude) }
        } catch (_: Exception) {
            null
        }
    }
}