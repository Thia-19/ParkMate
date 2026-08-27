package com.example.parkmate

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.parkmate.service.RouteTrackingService
import com.example.parkmate.permissions.RouteTrackingPermissions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BackgroundTaskManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun startRouteTracking(sessionId: Int) {
        check(RouteTrackingPermissions.hasRequiredPermissions(context)) {
            "Route tracking permissions are required before starting the service."
        }
        sendCommandToService(RouteTrackingService.ACTION_START, sessionId)
    }

    fun stopRouteTracking(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_CANCEL, sessionId)
    }

    fun markAsArrived(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_MANUAL_ARRIVED, sessionId)
    }

    fun promptConfirmation(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_PROMPT_CONFIRMATION, sessionId)
    }

    fun resumeTracking(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_RESUME_TRACKING, sessionId)
    }

    private fun sendCommandToService(action: String, sessionId: Int) {
        val intent = Intent(context, RouteTrackingService::class.java).apply {
            this.action = action
            putExtra(RouteTrackingService.EXTRA_SESSION_ID, sessionId)
        }
        ContextCompat.startForegroundService(context, intent)
    }
}