package com.example.parkmate.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.parkmate.MainActivity
import com.example.parkmate.R
import com.example.parkmate.notifications.NotificationsHelper.createActionPendingIntent
import com.example.parkmate.service.receiver.RouteTrackingActionReceiver
import com.example.parkmate.service.RouteTrackingService
object NotificationsHelper {

    // Expiry Alerts: fixed tickets
    const val PARKING_EXPIRY_CHANNEL_ID = "parking_expiry_channel"
    const val PARKING_EXPIRY_NOTIFICATION_ID_BASE = 5_000

    // Ongoing parkings
    const val ONGOING_PARKING_CHANNEL_ID = "ongoing_parking_channel"
    const val ONGOING_PARKING_NOTIFICATION_ID = 6_000

    // Activity Recognition: parking effort
    const val CHANNEL_ID = "route_tracking_channel"
    const val NOTIFICATION_ID = 7_201
    const val SAVED_ROUTE_NOTIFICATION_ID = 7_202

    // Activity Recognition: vehicle to pedestrian parking session
    const val TRANSITION_CHANNEL_ID = "vehicle_transition_channel"
    const val TRANSITION_CHANNEL_NAME = "Vehicle-pedestrian transition"
    const val TRANSITION_NOTIFICATION_ID = 2001

    // Creations
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

    fun createTransitionNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TRANSITION_CHANNEL_ID,
                TRANSITION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // Banner visibile e suono!
            ).apply {
                description = "Notifiche per la registrazione e chiusura della sosta"
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createParkingExpiryNotificationChannel(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Avvisi scadenza parcheggi"
            val descriptionText = "Notifiche per pre-scadenza e scadenza dei ticketz"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(PARKING_EXPIRY_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createOngoingParkingNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ONGOING_PARKING_CHANNEL_ID,
                "Promemoria Parcheggio Attivo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifiche periodiche sullo stato del parcheggio attivo"
                setShowBadge(false)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Cancel
    // In NotificationsHelper.kt

    // 1. Cancella la notifica di transizione veicolo->pedone (Home)
    fun cancelTransitionNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(TRANSITION_NOTIFICATION_ID)
    }

    fun cancelOngoingParkingNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(ONGOING_PARKING_NOTIFICATION_ID)
    }

    // 2. Cancella la notifica del tracciamento percorso
    fun cancelTrackingNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    // Notification builds

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

    fun buildVehicleTransitionNotification (
        context: Context,
        hasActiveSessions: Boolean
    ): Notification {
        val destinationRoute = if (hasActiveSessions) "home_parkings" else "new_parking"
        val requestCode = if (hasActiveSessions) 201 else 202

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", destinationRoute)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = context.getString(R.string.home_AR_title)
        val contentText = if (hasActiveSessions){
            context.getString(R.string.home_AR_action)
        } else {
            context.getString(R.string.home_AR_action_2)
        }
        val primaryButtonLabel = if (hasActiveSessions) context.getString(R.string.home_AR_handle_parkings) else context.getString(R.string.home_AR_add)

        return NotificationCompat.Builder(context, TRANSITION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_park)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                R.drawable.ic_park,
                primaryButtonLabel,
                contentPendingIntent
            )
            .addAction(
                0,
                context.getString(R.string.home_AR_dismiss),
                createDismissPendingIntent(context)
            )
            .build()
    }

    fun buildOngoingParkingNotification(
        context: Context,
        title: String,
        contentText: String
    ): Notification {
        createOngoingParkingNotificationChannel(context)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            ONGOING_PARKING_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, ONGOING_PARKING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_park)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
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
    private fun baseBuilder(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_route_tracking)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
    }

    fun buildWorkerNotification(context: Context, title: String, content: String): Notification {
        return baseBuilder(context)
            .setContentTitle(title)
            .setContentText(content)
            .build()
    }

    // Intent Creation

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

    private fun createDismissPendingIntent(context: Context): PendingIntent {
        val dismissIntent = Intent(context, RouteTrackingActionReceiver::class.java).apply {
            action = RouteTrackingActionReceiver.ACTION_DISMISS_PROMPT
        }
        return PendingIntent.getBroadcast(
            context,
            102,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // NOTIFICA - show
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

    fun showVehicleTransitionNotification(
        context: Context,
        hasActiveSessions: Boolean
    ) {
        createTransitionNotificationChannel(context)

        val notification = buildVehicleTransitionNotification(context, hasActiveSessions)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            NotificationManagerCompat.from(context).notify(TRANSITION_NOTIFICATION_ID, notification)
        }
    }

    fun showExpiryNotification(
        context: Context,
        notificationId: Int,
        title: String,
        content: String
    ){
        createParkingExpiryNotificationChannel(context)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PARKING_EXPIRY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_parking_marker)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun showOngoingParkingNotification(
        context: Context,
        title: String,
        contentText: String
    ) {
        val notification = buildOngoingParkingNotification(context, title, contentText)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(
                ONGOING_PARKING_NOTIFICATION_ID,
                notification
            )
        }
    }
}