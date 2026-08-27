package com.example.parkmate.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ServiceCompat
import com.example.parkmate.service.location_tracking.arrival.ArrivalActivityRecognitionManager
import com.example.parkmate.core.domain.use_case.parking_effort.route_tracking.CancelRouteTrackingUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.route_tracking.CompleteRouteTrackingUseCase
import com.example.parkmate.core.domain.use_case.parking_effort.route_tracking.RouteTrackingStartResult
import com.example.parkmate.core.domain.use_case.parking_effort.route_tracking.StartRouteTrackingUseCase
import com.example.parkmate.notifications.NotificationsHelper
import com.example.parkmate.service.location_tracking.CurrentPositionProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RouteTrackingService : Service() {

    @Inject lateinit var startRouteTracking: StartRouteTrackingUseCase
    @Inject lateinit var completeRouteTracking: CompleteRouteTrackingUseCase
    @Inject lateinit var cancelRouteTracking: CancelRouteTrackingUseCase
    @Inject lateinit var currentPositionProvider: CurrentPositionProvider
    @Inject lateinit var activityRecognition: ArrivalActivityRecognitionManager

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    inner class LocalBinder : Binder() {
        fun getService(): RouteTrackingService = this@RouteTrackingService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        NotificationsHelper.createNotificationChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand with action: ${intent?.action}")

        val sessionId = intent?.getIntExtra(EXTRA_SESSION_ID, NO_SESSION_ID) ?: NO_SESSION_ID
        if (sessionId == NO_SESSION_ID) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        val action = intent?.action
        startOrUpdateForeground(action, sessionId)

        when (action) {
            ACTION_START -> serviceScope.launch { handleStart(sessionId, startId) }
            ACTION_MANUAL_ARRIVED -> serviceScope.launch { handleComplete(sessionId, startId) }
            ACTION_CANCEL -> serviceScope.launch { handleCancel(sessionId, startId) }
            ACTION_PROMPT_CONFIRMATION,
            ACTION_RESUME_TRACKING -> {
            }
            else -> stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    private fun startOrUpdateForeground(action: String?, sessionId: Int) {
        val isConfirmation = action == ACTION_PROMPT_CONFIRMATION

        val notification = if (isConfirmation) {
            NotificationsHelper.buildConfirmationNotification(this, sessionId)
        } else {
            NotificationsHelper.buildTrackingNotification(this, sessionId)
        }

        val foregroundServiceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        } else {
            0
        }

        ServiceCompat.startForeground(
            this,
            NotificationsHelper.NOTIFICATION_ID,
            notification,
            foregroundServiceType
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun handleStart(sessionId: Int, startId: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("RouteTrackingService", "Permesso ACTIVITY_RECOGNITION mancante al momento dell'avvio")
                return
            }
        }

        try {
            val result = startRouteTracking(sessionId)
            Log.d("RouteTrackingService", "Risultato UseCase: $result")

            when (result) {
                RouteTrackingStartResult.Started,
                RouteTrackingStartResult.AlreadyStarted,
                RouteTrackingStartResult.AlreadyCompleted -> {
                    activityRecognition.start(sessionId)
                }
                else -> {
                    Log.e("RouteTrackingService", "Il service voleva spegnersi a causa di: $result")
                    // stopSelf(startId) <-- Lascialo commentato per il test
                }
            }
        } catch (e: Exception) {
            Log.e("RouteTrackingService", "Errore critico in handleStart", e)
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private suspend fun handleComplete(sessionId: Int, startId: Int) {
        try {
            val position = currentPositionProvider.getCurrentPosition()
            if (position != null) {
                completeRouteTracking(sessionId, position)
            }
        } finally {
            cleanup(sessionId, startId)
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private suspend fun handleCancel(sessionId: Int, startId: Int) {
        try {
            cancelRouteTracking(sessionId)
        } finally {
            cleanup(sessionId, startId)
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private suspend fun cleanup(sessionId: Int, startId: Int) {
        activityRecognition.stop(sessionId)
        NotificationsHelper.cancelNotification(this)
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf(startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "RouteTrackingService"

        const val EXTRA_SESSION_ID = "extra_session_id"
        const val NO_SESSION_ID = -1

        const val ACTION_START = "action_start"
        const val ACTION_MANUAL_ARRIVED = "action_manual_arrived"
        const val ACTION_CANCEL = "action_cancel"
        const val ACTION_PROMPT_CONFIRMATION = "action_prompt_confirmation"
        const val ACTION_RESUME_TRACKING = "action_resume_tracking"
    }
}