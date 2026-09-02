package com.example.parkmate.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.parkmate.BackgroundTaskManager
import com.example.parkmate.service.RouteTrackingService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** FUNZIONALITA':
 * cattura le azioni provenienti dai pulsanti delle notifiche
 * definite in NotificationsHelper e le inoltra al BackgroundTaskManager
 */


@AndroidEntryPoint
class RouteTrackingActionReceiver : BroadcastReceiver() {

    @Inject lateinit var backgroundTaskManager: BackgroundTaskManager

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val sessionId = intent.getIntExtra(RouteTrackingService.EXTRA_SESSION_ID, RouteTrackingService.NO_SESSION_ID)

        if (sessionId == RouteTrackingService.NO_SESSION_ID) return

        when (action) {
            ACTION_ARRIVED -> {
                backgroundTaskManager.markAsArrived(sessionId)
            }
            ACTION_CANCEL -> {
                backgroundTaskManager.cancelRouteTracking(sessionId)
            }
            ACTION_RESUME -> {
                backgroundTaskManager.resumeTracking(sessionId)
            }
        }
    }

    companion object {
        const val ACTION_ARRIVED = "com.example.parkmate.action.NOTIFICATION_ARRIVED"
        const val ACTION_CANCEL = "com.example.parkmate.action.NOTIFICATION_CANCEL"
        const val ACTION_RESUME = "com.example.parkmate.action.NOTIFICATION_RESUME"
        const val ACTION_DISMISS_PROMPT = "action_dismiss_prompt"

        // Not used
        const val EXTRA_SESSION_ID = "extra_session_id"

        const val ACTION_PROMPT_NEW_PARK = "action_prompt_new_park"
    }
}