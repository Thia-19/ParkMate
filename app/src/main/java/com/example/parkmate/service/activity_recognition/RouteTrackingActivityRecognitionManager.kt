package com.example.parkmate.service.activity_recognition

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.parkmate.service.RouteTrackingService
import com.example.parkmate.service.receiver.ActivityRecognitionReceiver
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteTrackingActivityRecognitionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private fun createPendingIntent(sessionId: Int): PendingIntent {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java).apply {
            action = ActivityRecognitionReceiver.ACTION_ACTIVITY_TRANSITION
            putExtra(RouteTrackingService.EXTRA_SESSION_ID, sessionId)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getBroadcast(context, sessionId, intent, flags)
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun startMonitoring(sessionId: Int) {
        val transitions = listOf(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )
        val request = ActivityTransitionRequest(transitions)
        val pendingIntent = createPendingIntent(sessionId)

        ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(request, pendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "Activity Transition Updates registrate con successo per sessione #$sessionId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Registrazione Activity Transition Updates FALLITA per sessione #$sessionId", e)
            }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun stopMonitoring(sessionId: Int) {
        val pendingIntent = createPendingIntent(sessionId)
        ActivityRecognition.getClient(context)
            .removeActivityTransitionUpdates(pendingIntent)
            .addOnFailureListener { e ->
                Log.e(TAG, "Rimozione Activity Transition Updates fallita per sessione #$sessionId", e)
            }
    }

    companion object {
        private const val TAG = "RouteTrackingARManager"
    }
}