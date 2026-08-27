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
        if (sessionId == -1) return

        when (intent.action) {
            ACTION_ARRIVED -> backgroundTaskManager.markAsArrived(sessionId)
            ACTION_CANCEL -> backgroundTaskManager.stopRouteTracking(sessionId)
            ACTION_RESUME -> backgroundTaskManager.resumeTracking(sessionId)
        }
    }

    companion object {
        const val ACTION_ARRIVED = "route_tracking_arrived"
        const val ACTION_CANCEL = "route_tracking_cancel"
        const val ACTION_RESUME = "route_tracking_resume"
        const val EXTRA_SESSION_ID = "extra_session_id"
    }
}