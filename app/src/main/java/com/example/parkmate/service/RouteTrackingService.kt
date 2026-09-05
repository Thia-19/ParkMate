package com.example.parkmate.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.use_case.parking_effort.route_tracking_logic.CompleteRouteTrackingUseCase
import com.example.parkmate.notifications.NotificationsHelper
import com.example.parkmate.permissions.RouteTrackingPermissions
import com.example.parkmate.service.activity_recognition.RouteTrackingActivityRecognitionManager
import com.example.parkmate.service.activity_recognition.VehicleToPedestrianActivityRecognitionManager
import com.example.parkmate.service.data_providers.location.LocationException
import com.example.parkmate.service.data_providers.location.LocationProvider
import com.example.parkmate.service.data_providers.steps.StepCounterManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RouteTrackingService : Service() {
    @Inject lateinit var locationProvider: LocationProvider
    @Inject lateinit var stepCounterManager: StepCounterManager
    @Inject lateinit var activityRecognitionManager: RouteTrackingActivityRecognitionManager
    @Inject lateinit var vehicleToPedestrianManager: VehicleToPedestrianActivityRecognitionManager
    @Inject lateinit var completeRouteTrackingUseCase: CompleteRouteTrackingUseCase

    private val serviceScope = CoroutineScope(Dispatchers.Default.limitedParallelism(1) + SupervisorJob())
    private var locationJob: Job? = null
    private var stepsJob: Job? = null

    private var currentSessionId: Int = NO_SESSION_ID
    private var isTracking: Boolean = false
    private var startTimeMillis: Long = 0L

    private var startPosition: Position? = null
    private var currentPosition: Position? = null
    private var totalDistanceMeters: Double = 0.0

    private var initialStepsCount: Int? = null
    private var totalSteps: Int = 0

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresPermission(allOf = [Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACTIVITY_RECOGNITION])
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY
        val sessionId = intent.getIntExtra(EXTRA_SESSION_ID, NO_SESSION_ID)

        when (action) {
            ACTION_START -> handleStartCommand(sessionId)
            ACTION_STOP_AND_SAVE -> handleStopAndSaveCommand(sessionId)
            ACTION_CANCEL -> handleCancelCommand()
            ACTION_PROMPT_CONFIRMATION -> handlePromptConfirmationCommand(sessionId)
            ACTION_RESUME_TRACKING -> handleResumeTrackingCommand()
            ACTION_DISMISS_TRANSITION_PROMPT -> {
                NotificationsHelper.cancelTransitionNotification(this)
            }
        }

        return START_NOT_STICKY
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private fun handleStartCommand(sessionId: Int) {
        val targetSessionId = if (sessionId != NO_SESSION_ID) sessionId else currentSessionId
        startForegroundSafely(targetSessionId)

        if (sessionId == NO_SESSION_ID) {
            cleanupAndStopService()
            return
        }

        if (!RouteTrackingPermissions.hasAllTrackingPermissions(this)) {
            NotificationsHelper.showErrorNotification(
                this,
                getString(R.string.permissions_revoked),
                getString(R.string.route_tracking_stopped)
            )
            cleanupAndStopService()
            return
        }

        if (isTracking) return

        currentSessionId = sessionId
        isTracking = true
        startTimeMillis = System.currentTimeMillis()

        startLocationTracking()
        startStepsTracking()

        try {
            vehicleToPedestrianManager.stopMonitoring()
            activityRecognitionManager.startMonitoring(currentSessionId)
        } catch (e: SecurityException) {
            NotificationsHelper.showErrorNotification(
                this,
                getString(R.string.tracking_limited)
            )
        }
    }


    private fun startForegroundSafely(sessionId: Int) {
        val safeSessionId = if (sessionId != NO_SESSION_ID) sessionId else 0
        val notification = NotificationsHelper.buildTrackingNotification(this, safeSessionId)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NotificationsHelper.ROUTE_TRACKING_NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NotificationsHelper.ROUTE_TRACKING_NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                )
            } else {
                startForeground(NotificationsHelper.ROUTE_TRACKING_NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            stopSelf()
        }
    }

    private fun startLocationTracking() {
        locationJob = serviceScope.launch {
            while (isActive && isTracking) {
                try {
                    if (!RouteTrackingPermissions.hasAllTrackingPermissions(this@RouteTrackingService)) {
                        throw SecurityException("Permessi revocati durante l'esecuzione.")
                    }

                    val newPosition = locationProvider.getCurrentPosition()

                    if (startPosition == null) {
                        startPosition = newPosition
                    } else if (currentPosition != null) {

                        val results = FloatArray(1)
                        android.location.Location.distanceBetween(
                            currentPosition!!.latitude, currentPosition!!.longitude,
                            newPosition.latitude, newPosition.longitude,
                            results
                        )
                        totalDistanceMeters += results[0].toDouble()
                    }

                    currentPosition = newPosition

                } catch (e: LocationException.LocationDisabledException) {
                    NotificationsHelper.showErrorNotification(this@RouteTrackingService, "GPS disattivato durante il tracciamento. Dati parziali salvati.")
                    handleStopAndSaveCommand(currentSessionId)
                    return@launch
                } catch (e: LocationException.LocationUnavailableException) {
                    Log.i("RouteTrackingService", "Location non disponibile al momento: ${e.message}")
                } catch (e: SecurityException) {
                    NotificationsHelper.showErrorNotification(this@RouteTrackingService, "Permessi revocati. Tracciamento interrotto.")
                    handleStopAndSaveCommand(currentSessionId)
                    return@launch
                } catch (e: Exception) {
                    Log.i("RouteTrackingService", "Errore durante il tracciamento: ${e.message}")
                }

                delay(10_000L)
            }
        }
    }

    private fun startStepsTracking() {
        stepsJob = serviceScope.launch {
            stepCounterManager.getStepsCount()
                .catch { e ->
                    Log.i("RouteTrackingService", "Errore durante il conteggio dei passi: ${e.message}")
                }
                .collect { currentAbsoluteSteps ->
                    val initial = initialStepsCount ?: currentAbsoluteSteps.also { initialStepsCount = it }
                    totalSteps = (currentAbsoluteSteps - initial).coerceAtLeast(0)
                }
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private fun handleStopAndSaveCommand(sessionId: Int) {
        if (sessionId != currentSessionId && currentSessionId != NO_SESSION_ID) return

        serviceScope.launch {
            val endTime = System.currentTimeMillis()
            val timeFrame = endTime - startTimeMillis

            val startAlt = startPosition?.altitude ?: 0.0
            val endAlt = currentPosition?.altitude ?: 0.0
            val elevationGain = if (endAlt > startAlt) endAlt - startAlt else 0.0

            try {
                Log.i("RouteTrackingService", "Saving route tracking data...")
                Log.i("RouteTrackingService", "Total distance: $totalDistanceMeters meters")
                Log.i("RouteTrackingService", "Total steps: $totalSteps")
                Log.i("RouteTrackingService", "Elevation gain: $elevationGain meters")
                Log.i("RouteTrackingService", "Time frame: $timeFrame milliseconds")

                completeRouteTrackingUseCase(
                    sessionId = currentSessionId,
                    timeFrame = timeFrame,
                    destination = currentPosition,
                    realDistanceMeters = totalDistanceMeters,
                    totalSteps = totalSteps,
                    elevationGain = elevationGain
                )
            } catch (e: Exception) {
                NotificationsHelper.showErrorNotification(
                    this@RouteTrackingService,
                    getString(R.string.error_saving_route_tracking)
                )
            } finally {
                cleanupAndStopService()
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private fun handleCancelCommand() {
        cleanupAndStopService()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun handlePromptConfirmationCommand(sessionId: Int) {
        val notification = NotificationsHelper.buildConfirmationNotification(this, sessionId)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NotificationsHelper.CONFIRMATION_NOTIFICATION_ID, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun handleResumeTrackingCommand() {
        NotificationsHelper.cancelConfirmationNotification(this)
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private fun stopTracking() {
        locationJob?.cancel()
        stepsJob?.cancel()
        isTracking = false
        try {
            activityRecognitionManager.stopMonitoring(currentSessionId)
            vehicleToPedestrianManager.startMonitoring()
        } catch (e: SecurityException) {
            NotificationsHelper.showErrorNotification(
                this,
                getString(R.string.tracking_limited)
            )
            resetTracking()

        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    private fun resetTracking() {
        startPosition = null
        currentPosition = null
        totalDistanceMeters = 0.0
        initialStepsCount = null
        totalSteps = 0
        startTimeMillis = 0L
        currentSessionId = NO_SESSION_ID
    }

    @SuppressLint("MissingPermission")
    private fun cleanupAndStopService() {
        stopTracking()
        resetTracking()
        NotificationsHelper.cancelTrackingNotification(this)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        cleanupAndStopService()
    }

    companion object {
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val NO_SESSION_ID = -5
        const val ACTION_START = "com.example.parkmate.action.START"
        const val ACTION_STOP_AND_SAVE = "com.example.parkmate.action.STOP_AND_SAVE"
        const val ACTION_CANCEL = "com.example.parkmate.action.CANCEL"
        const val ACTION_PROMPT_CONFIRMATION = "com.example.parkmate.action.PROMPT_CONFIRMATION"
        const val ACTION_RESUME_TRACKING = "com.example.parkmate.action.RESUME_TRACKING"
        const val ACTION_DISMISS_TRANSITION_PROMPT = "action_dismiss_transition_prompt"
    }
}






