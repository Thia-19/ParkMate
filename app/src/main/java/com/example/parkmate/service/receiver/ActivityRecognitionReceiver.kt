package com.example.parkmate.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.parkmate.BackgroundTaskManager
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
        if (!ActivityTransitionResult.hasResult(intent)) return
        val sessionId = intent.getIntExtra(EXTRA_SESSION_ID, NO_SESSION_ID)
        if (sessionId == NO_SESSION_ID) return

        val hasArrived = ActivityTransitionResult.extractResult(intent)
            ?.transitionEvents
            ?.any { event ->
                event.activityType == DetectedActivity.STILL &&
                        event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
            } == true

        if (!hasArrived) return

        backgroundTaskManager.promptConfirmation(sessionId)
    }

    companion object {
        const val ACTION_ACTIVITY_TRANSITION = "activity_transition"
        const val EXTRA_SESSION_ID = "extra_session_id"
        private const val NO_SESSION_ID = -1
    }
}