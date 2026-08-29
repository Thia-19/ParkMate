package com.example.parkmate.service.activity_recognition

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.parkmate.permissions.RouteTrackingPermissions
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.example.parkmate.service.receiver.VehicleToPedestrianReceiver

@Singleton
class VehicleToPedestrianActivityRecognitionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client = ActivityRecognition.getClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, VehicleToPedestrianReceiver::class.java).apply {
            action = VehicleToPedestrianReceiver.ACTION_VEHICLE_TRANSITION
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        PendingIntent.getBroadcast(context, 1001, intent, flags)
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun startMonitoring() {
        if (!RouteTrackingPermissions.hasRequiredPermissions(context)) return

        context.getSharedPreferences("ar_state_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("key_was_in_vehicle", false)
            .apply()

        val transitions = listOf(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        val request = ActivityTransitionRequest(transitions)

        try {
            client.requestActivityTransitionUpdates(request, pendingIntent)
        } catch (_: SecurityException) {
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun stopMonitoring() {
        try {
            client.removeActivityTransitionUpdates(pendingIntent)
        } catch (_: Exception) {
        }
    }
}