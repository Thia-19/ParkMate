package com.example.parkmate.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.parkmate.R
import com.example.parkmate.service.receiver.RouteTrackingActionReceiver
import com.example.parkmate.service.RouteTrackingService

object NotificationsHelper {

    const val CHANNEL_ID = "route_tracking_channel"
    const val NOTIFICATION_ID = 7_201
    const val SAVED_ROUTE_NOTIFICATION_ID = 7_202

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.route_tracking_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.route_tracking_channel_description)
                setShowBadge(false)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildTrackingNotification(context: Context, sessionId: Int): Notification {
        return baseBuilder(context)
            .setContentTitle(context.getString(R.string.route_tracking_notification_title))
            .setContentText(context.getString(R.string.route_tracking_notification_message))
            .addAction(
                R.drawable.ic_route_tracking,
                "Arrived",
                createActionPendingIntent(context, RouteTrackingActionReceiver.ACTION_ARRIVED, sessionId)
            )
            .addAction(
                R.drawable.ic_route_tracking,
                "Cancel",
                createActionPendingIntent(context, RouteTrackingActionReceiver.ACTION_CANCEL, sessionId)
            )
            .build()
    }

    fun buildConfirmationNotification(context: Context, sessionId: Int): Notification {
        return baseBuilder(context)
            .setContentTitle("Destination reached?")
            .setContentText("We noticed you stopped walking. Have you arrived?")
            .addAction(
                R.drawable.ic_route_tracking,
                "Yes, Arrived",
                createActionPendingIntent(context, RouteTrackingActionReceiver.ACTION_ARRIVED, sessionId)
            )
            .addAction(
                R.drawable.ic_route_tracking,
                "No, Resume",
                createActionPendingIntent(context, RouteTrackingActionReceiver.ACTION_RESUME, sessionId)
            )
            .build()
    }

    fun cancelNotification(context: Context, notificationId: Int = NOTIFICATION_ID) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun baseBuilder(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_route_tracking)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
    }

    private fun createActionPendingIntent(context: Context, action: String, sessionId: Int): PendingIntent {
        val intent = Intent(context, RouteTrackingActionReceiver::class.java).apply {
            this.action = action
            data = "parkmate://route-tracking/notification/$action/$sessionId".toUri()
            putExtra(RouteTrackingService.EXTRA_SESSION_ID, sessionId)
        }
        return PendingIntent.getBroadcast(
            context,
            action.hashCode() + sessionId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun buildWorkerNotification(context: Context, title: String, content: String): Notification {
        return baseBuilder(context)
            .setContentTitle(title)
            .setContentText(content)
            .build()
    }

    // NOTIFICA
    fun showRouteSavedNotification(context: Context) {
        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_route_tracking) // Usa l'icona adatta
            .setContentTitle("Mappa Aggiornata")
            .setContentText("Il tuo percorso è stato salvato!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // La notifica sparisce quando l'utente ci clicca o fa swipe
            .build()

        try {
            NotificationManagerCompat.from(context).notify(SAVED_ROUTE_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Fallback nel caso in cui i permessi non siano garantiti
            e.printStackTrace()
        }
    }
}