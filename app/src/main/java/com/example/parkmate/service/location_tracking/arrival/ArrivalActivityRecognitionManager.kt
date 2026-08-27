package com.example.parkmate.service.location_tracking.arrival

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import com.example.parkmate.permissions.RouteTrackingPermissions
import com.example.parkmate.service.receiver.ActivityRecognitionReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArrivalActivityRecognitionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client = ActivityRecognition.getClient(context)

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    suspend fun start(sessionId: Int) {
        check(RouteTrackingPermissions.hasRequiredPermissions(context)) {
            "Route tracking permissions are required."
        }
        client.requestActivityTransitionUpdates(
            ActivityTransitionRequest(listOf(stillEnteredTransition)),
            createPendingIntent(sessionId)
        ).await()
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    suspend fun stop(sessionId: Int) {
        client.removeActivityTransitionUpdates(createPendingIntent(sessionId)).await()
    }

    private fun createPendingIntent(sessionId: Int): PendingIntent {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java).apply {
            action = ActivityRecognitionReceiver.Companion.ACTION_ACTIVITY_TRANSITION
            data = "parkmate://route-tracking/activity/$sessionId".toUri()
            putExtra(ActivityRecognitionReceiver.Companion.EXTRA_SESSION_ID, sessionId)
        }
        return PendingIntent.getBroadcast(
            context,
            sessionId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        val stillEnteredTransition = ActivityTransition.Builder()
            .setActivityType(DetectedActivity.STILL)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build()
    }
}