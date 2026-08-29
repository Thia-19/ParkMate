package com.example.parkmate.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import coil.util.Logger
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.notifications.NotificationsHelper
import com.example.parkmate.notifications.NotificationsHelper.showVehicleTransitionNotification
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class VehicleToPedestrianReceiver : BroadcastReceiver() {

    @Inject
    lateinit var parkingRepository: ParkingSessionRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_VEHICLE_TRANSITION) return

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent) ?: return
            val prefs = context.getSharedPreferences("ar_state_prefs", Context.MODE_PRIVATE)

            for (event in result.transitionEvents) {
                val activityType = event.activityType
                val transitionType = event.transitionType

                if ((activityType == DetectedActivity.IN_VEHICLE || activityType == DetectedActivity.ON_BICYCLE) &&
                    (transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER || transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                ) {
                    prefs.edit().putBoolean(KEY_WAS_IN_VEHICLE, true).apply()
                }

                if ((activityType == DetectedActivity.WALKING || activityType == DetectedActivity.STILL) &&
                    transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                ) {
                    val wasInVehicle = prefs.getBoolean(KEY_WAS_IN_VEHICLE, false)

                    if (wasInVehicle) {
                        prefs.edit().putBoolean(KEY_WAS_IN_VEHICLE, false).apply()

                        val pendingResult = goAsync()
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val activeSessions = parkingRepository.getActiveParkingSessions().first()
                                val hasActiveSessions = activeSessions.isNotEmpty()

                                withContext(Dispatchers.Main) {
                                    showVehicleTransitionNotification(
                                        context = context,
                                        hasActiveSessions = hasActiveSessions
                                    )
                                }
                            } catch (e: Exception) {
                                // e.printStackTrace()
                            } finally {
                                pendingResult.finish()
                            }
                        }
                        break
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_VEHICLE_TRANSITION = "com.example.parkmate.ACTION_VEHICLE_TRANSITION"
        private const val KEY_WAS_IN_VEHICLE = "key_was_in_vehicle"
    }
}