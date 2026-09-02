package com.example.parkmate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.notifications.NotificationsHelper
import com.example.parkmate.service.RouteTrackingService
import com.example.parkmate.service.receiver.ParkingExpiryReceiver
import com.example.parkmate.workManager.OngoingParkingWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundTaskManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /** Parking Expiry Alerts (AlarmManager) **/

    fun scheduleParkingExpiryAlerts(
        session: ParkingSession,
        warningMinutes: Int = DEFAULT_WARNING_MINUTES
    ) {
        val expiryTime = session.expiryTime ?: return
        if (!session.isActive || session.parkingType != ParkingType.FIXED_TICKET) return

        val now = System.currentTimeMillis()
        val warningTime = expiryTime - (warningMinutes * 60 * 1000L)

        if (warningTime > now) {
            val pendingIntent = createExpiryPendingIntent(session.id, isExact = false)
            setExactAlarm(warningTime, pendingIntent)
        }

        if (expiryTime > now) {
            val pendingIntent = createExpiryPendingIntent(session.id, isExact = true)
            setExactAlarm(expiryTime, pendingIntent)
        }
    }

    fun cancelParkingExpiryAlerts(sessionId: Int) {
        listOf(false, true).forEach { isExact ->
            createExpiryPendingIntent(
                sessionId,
                isExact,
                flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )?.let { alarmManager.cancel(it) }
        }
    }

    private fun createExpiryPendingIntent(
        sessionId: Int,
        isExact: Boolean,
        flags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    ): PendingIntent? {
        val intent = Intent(context, ParkingExpiryReceiver::class.java).apply {
            putExtra(ParkingExpiryReceiver.EXTRA_SESSION_ID, sessionId)
            putExtra(ParkingExpiryReceiver.EXTRA_IS_EXACT_EXPIRY, isExact)
        }
        val requestCode = getPendingIntentRequestCode(sessionId, isExact)
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }

    private fun setExactAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent?) {
        pendingIntent ?: return

        val canScheduleExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun getPendingIntentRequestCode(sessionId: Int, isExact: Boolean): Int {
        return if (isExact) (sessionId * 10) + 1 else (sessionId * 10) + 2
    }

    /** Ongoing Parking Reminders (WorkManager) **/

    fun startOngoingParkingReminder(sessionId: Int) {
        val workTag = getOngoingWorkTag(sessionId)

        val inputData = Data.Builder()
            .putInt(OngoingParkingWorker.EXTRA_SESSION_ID, sessionId)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<OngoingParkingWorker>(
            REPEAT_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setInputData(inputData)
            .addTag(workTag)
            .build()

        workManager.enqueueUniquePeriodicWork(
            workTag,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest
        )
    }

    fun stopOngoingParkingReminder(sessionId: Int) {
        workManager.cancelUniqueWork(getOngoingWorkTag(sessionId))
        NotificationsHelper.cancelOngoingParkingNotification(context)
    }

    private fun getOngoingWorkTag(sessionId: Int): String =
        "${OngoingParkingWorker.WORK_TAG_PREFIX}$sessionId"


    /** Route Tracking Service Commands **/
    fun startRouteTracking(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_START, sessionId)
    }

    fun stopRouteTracking(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_STOP_AND_SAVE, sessionId)
    }

    fun markAsArrived(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_STOP_AND_SAVE, sessionId)
    }

    fun cancelRouteTracking(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_CANCEL, sessionId)
    }

    fun promptConfirmation(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_PROMPT_CONFIRMATION, sessionId)
    }

    fun resumeTracking(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_RESUME_TRACKING, sessionId)
    }

    fun handlePromptNewPark(sessionId: Int) {
        sendCommandToService(RouteTrackingService.ACTION_PROMPT_NEW_PARK, sessionId)
    }

    fun dismissVehicleTransitionPrompt() {
        sendCommandToService(RouteTrackingService.ACTION_DISMISS_TRANSITION_PROMPT, -5)
    }

    private fun sendCommandToService(
        actionStr: String,
        sessionId: Int
    ) {
        val intent = Intent(
            context,
            RouteTrackingService::class.java
        ).apply {
            action = actionStr
            putExtra(
                RouteTrackingService.EXTRA_SESSION_ID,
                sessionId
            )
        }

        if (actionStr == RouteTrackingService.ACTION_START) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            context.startService(intent)
        }
    }

    companion object {
        private const val DEFAULT_WARNING_MINUTES = 10
        private const val REPEAT_INTERVAL_MINUTES = 15L
    }
}