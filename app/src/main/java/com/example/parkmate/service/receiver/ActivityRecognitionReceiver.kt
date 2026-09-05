package com.example.parkmate.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.parkmate.BackgroundTaskManager
import com.example.parkmate.service.RouteTrackingService
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ActivityRecognitionReceiver : BroadcastReceiver() {
    @Inject
    lateinit var backgroundTaskManager: BackgroundTaskManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null && intent.action != ACTION_ACTIVITY_TRANSITION) {
            return
        }

        if (!ActivityTransitionResult.hasResult(intent)) {
            Log.w(TAG, "No ActivityTransitionResult nell'intent")
            return
        }

        val result = ActivityTransitionResult.extractResult(intent) ?: return

        val sessionId = intent.getIntExtra(
            RouteTrackingService.EXTRA_SESSION_ID,
            RouteTrackingService.NO_SESSION_ID
        )

        if (sessionId == RouteTrackingService.NO_SESSION_ID) {
            Log.w(TAG, "Non è presente un sessionId valido")
            return
        }

        val hasArrived = result.transitionEvents.any { event ->
            event.activityType == DetectedActivity.STILL &&
                    event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
        }

        val hasResumedMoving = result.transitionEvents.any { event ->
            event.activityType == DetectedActivity.WALKING &&
                    event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
        }


        if (hasArrived) {
            backgroundTaskManager.promptConfirmation(sessionId)
        }
        if (hasResumedMoving) {
            backgroundTaskManager.resumeTracking(sessionId)
        }
    }

    companion object {
        private const val TAG = "ActivityRecognitionReceiver"
        const val ACTION_ACTIVITY_TRANSITION = "com.example.parkmate.action.ACTIVITY_TRANSITION"
    }
}
