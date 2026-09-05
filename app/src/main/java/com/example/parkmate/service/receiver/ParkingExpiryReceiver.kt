package com.example.parkmate.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.parkmate.R
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.notifications.NotificationsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ParkingExpiryReceiver : BroadcastReceiver(){
    @Inject
    lateinit var parkingSessionRepository: ParkingSessionRepository

    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getIntExtra(EXTRA_SESSION_ID, -1)
        val isExactExpiry = intent.getBooleanExtra(EXTRA_IS_EXACT_EXPIRY, false)

        if (sessionId == -1) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val item = parkingSessionRepository.getParkingSessionWithVehicleById(sessionId)

                if (item != null && item.session.isActive) {
                    val vehicleName = item.vehicleName

                    val title: String
                    val content: String
                    val notificationId: Int

                    if (isExactExpiry) {
                        title = "${context.getString(R.string.notifications_expired)} ($vehicleName)"
                        content = context.getString(R.string.notifications_expired_desc)
                        notificationId = NotificationsHelper.PARKING_EXPIRY_NOTIFICATION_ID_BASE + sessionId
                    } else {
                        title = "${context.getString(R.string.notifications_expiry)} ($vehicleName)"
                        content = context.getString(R.string.notifications_expiry_desc)
                        notificationId = NotificationsHelper.PARKING_EXPIRY_NOTIFICATION_ID_BASE + sessionId + 100_000
                    }

                    NotificationsHelper.showExpiryNotification(context, notificationId, title, content)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val EXTRA_IS_EXACT_EXPIRY = "extra_is_exact_expiry"
    }
}