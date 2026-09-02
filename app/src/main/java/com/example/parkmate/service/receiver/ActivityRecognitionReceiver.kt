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

        Log.d(TAG, "Evento Activity Recognition ricevuto. Action: ${intent.action}")

        if (intent.action != null && intent.action != ACTION_ACTIVITY_TRANSITION) {
            Log.w(TAG, "Azione ignorata: ${intent.action}")
            return
        }

        if (!ActivityTransitionResult.hasResult(intent)) {
            Log.w(TAG, "Nessun ActivityTransitionResult valido nell'intent")
            return
        }


        val result = ActivityTransitionResult.extractResult(intent) ?: return

        val sessionId = intent.getIntExtra(
            RouteTrackingService.EXTRA_SESSION_ID,
            RouteTrackingService.NO_SESSION_ID
        )

        if (sessionId == RouteTrackingService.NO_SESSION_ID) {
            Log.e(TAG, "Impossibile mostrare conferma: Session ID non trovato nell'Intent")
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
            Log.i(
                TAG,
                "Stato 'STILL' rilevato per la sessione #$sessionId. Invio richiesta di conferma..."
            )
            backgroundTaskManager.promptConfirmation(sessionId)
        }

        if (hasResumedMoving) {
            Log.i(TAG, "Movimento ('WALKING') rilevato per la sessione #$sessionId.")
            // qui puoi decidere l'azione: es. annullare una conferma "sei arrivato?" ancora pendente

        }
    }

        companion object {
            private const val TAG = "ActivityRecReceiver"
            const val ACTION_ACTIVITY_TRANSITION = "com.example.parkmate.action.ACTIVITY_TRANSITION"
        }
    }
