package com.example.parkmate.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.parkmate.BackgroundTaskManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RouteTrackingActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var backgroundTaskManager: BackgroundTaskManager

    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getIntExtra(EXTRA_SESSION_ID, -1)

        when (intent.action) {
            ACTION_ARRIVED -> if (sessionId == -1) return backgroundTaskManager.markAsArrived(sessionId)
            ACTION_CANCEL -> if (sessionId == -1) return backgroundTaskManager.stopRouteTracking(sessionId)
            ACTION_RESUME -> if (sessionId == -1) return backgroundTaskManager.resumeTracking(sessionId)
            ACTION_PROMPT_NEW_PARK -> backgroundTaskManager.handlePromptNewPark(sessionId)
            ACTION_DISMISS_PROMPT -> backgroundTaskManager.dismissVehicleTransitionPrompt()
        }
    }

    companion object {
        const val ACTION_ARRIVED = "route_tracking_arrived"
        const val ACTION_CANCEL = "route_tracking_cancel"
        const val ACTION_RESUME = "route_tracking_resume"
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val ACTION_PROMPT_NEW_PARK = "action_prompt_new_park"
        const val ACTION_DISMISS_PROMPT = "action_dismiss_prompt"
    }
}