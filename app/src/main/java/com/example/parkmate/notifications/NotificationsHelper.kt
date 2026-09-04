package com.example.parkmate.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.parkmate.MainActivity
import com.example.parkmate.R
import com.example.parkmate.service.RouteTrackingService
import com.example.parkmate.service.receiver.RouteTrackingActionReceiver

object NotificationsHelper {
    const val TRANSITION_CHANNEL_NAME = "Vehicle-pedestrian transition"

    /** ID CANALI **/
    const val PARKING_EXPIRY_CHANNEL_ID = "parking_expiry_channel"
    const val ONGOING_PARKING_CHANNEL_ID = "ongoing_parking_channel"

    const val ROUTE_TRACKING_CHANNEL_ID = "route_tracking_channel"
    const val TRANSITION_CHANNEL_ID = "vehicle_transition_channel"

    const val ERROR_CHANNEL_ID = "error_notification_channel"

    /** ID NOTIFICHE **/
    const val PARKING_EXPIRY_NOTIFICATION_ID_BASE = 5_000

    // Ongoing parkings
    const val ONGOING_PARKING_NOTIFICATION_ID = 6_000
    const val ROUTE_TRACKING_NOTIFICATION_ID = 7_201
    const val SAVED_EFFORT_NOTIFICATION_ID = 7_202
    const val CONFIRMATION_NOTIFICATION_ID = 7_203
    const val PERMISSION_REVOKED_NOTIFICATION_ID = 7_204

    // Activity Recognition: vehicle to pedestrian parking session
    const val TRANSITION_NOTIFICATION_ID = 2001
    const val ERROR_NOTIFICATION_ID = 8_000

    /** CREAZIONE CANALI **/

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Tracciamento Percorso"
            val descriptionText = "Notifica persistente per il tracciamento offline del percorso e dei passi"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(
                ROUTE_TRACKING_CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
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
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.create_transition_channel)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createParkingExpiryNotificationChannel(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notifications_parking_expiry)
            val descriptionText = context.getString(R.string.notifications_parking_expiry_desc)
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
                context.getString(R.string.notifications_active),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notifications_active_desc)
                setShowBadge(false)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createErrorNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ERROR_CHANNEL_ID,
                "Notifiche di Errore",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canale dedicato alle segnalazioni di errore dell'applicazione"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /** CANCELLAZIONE NOTIFICHE **/
    fun cancelTransitionNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(TRANSITION_NOTIFICATION_ID)
    }

    fun cancelOngoingParkingNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(ONGOING_PARKING_NOTIFICATION_ID)
    }

    fun cancelTrackingNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(ROUTE_TRACKING_NOTIFICATION_ID)
        NotificationManagerCompat.from(context).cancel(CONFIRMATION_NOTIFICATION_ID)
    }

    fun cancelConfirmationNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(CONFIRMATION_NOTIFICATION_ID)
    }


    /** BUILDERS **/

    fun buildTrackingNotification(context: Context, sessionId: Int): Notification {
        createNotificationChannel(context)

        val stopIntent = Intent(context, RouteTrackingService::class.java).apply {
            action = RouteTrackingService.ACTION_STOP_AND_SAVE
            putExtra(RouteTrackingService.EXTRA_SESSION_ID, sessionId)
        }
        val stopPendingIntent = PendingIntent.getService(
            context, 301, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(context, RouteTrackingService::class.java).apply {
            action = RouteTrackingService.ACTION_CANCEL
            putExtra(RouteTrackingService.EXTRA_SESSION_ID, sessionId)
        }
        val cancelPendingIntent = PendingIntent.getService(
            context, 302, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return baseBuilder(context)
            .setChannelId(ROUTE_TRACKING_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.route_tracking_on))
            .setContentText(context.getString(R.string.sensors_in_action))
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .addAction(R.drawable.ic_check_circle, "Arrivato a destinazione", stopPendingIntent)
            .addAction(R.drawable.ic_cancel, "Annulla", cancelPendingIntent)
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
        createTransitionNotificationChannel(context)

        val confirmIntent = Intent(context, RouteTrackingService::class.java).apply {
            action = RouteTrackingService.ACTION_STOP_AND_SAVE
            putExtra(RouteTrackingService.EXTRA_SESSION_ID, sessionId)
        }
        val confirmPendingIntent = PendingIntent.getService(
            context, 401, confirmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resumeIntent = Intent(context, RouteTrackingService::class.java).apply {
            action = RouteTrackingService.ACTION_RESUME_TRACKING
            putExtra(RouteTrackingService.EXTRA_SESSION_ID, sessionId)
        }
        val resumePendingIntent = PendingIntent.getService(
            context, 402, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return baseBuilder(context)
            .setChannelId(TRANSITION_CHANNEL_ID)
            .setContentTitle("Sei arrivato a destinazione?")
            .setContentText("Abbiamo rilevato che sei fermo. Vuoi salvare il percorso?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_check_circle, "Sì, salva", confirmPendingIntent)
            .addAction(R.drawable.ic_cancel, "No, continua", resumePendingIntent)
            .setAutoCancel(true)
            .build()
    }

    fun buildWorkerNotification(context: Context, title: String, content: String): Notification {
        return baseBuilder(context)
            .setContentTitle(title)
            .setContentText(content)
            .build()
    }

    /** HELPERS **/

    private fun baseBuilder(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, ROUTE_TRACKING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_route_tracking)
            .setColor(ContextCompat.getColor(context, R.color.black))
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
            .setSmallIcon(R.drawable.ic_park)
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

    fun showPermissionRevokedNotification(context: Context) {

    }

    fun showErrorNotification(
        context: Context,
        errorMessage: String,
        title: String = "Errore"
    ) {
        createErrorNotificationChannel(context)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            ERROR_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ERROR_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_route_tracking)
            .setContentTitle(title)
            .setContentText(errorMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(errorMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ERROR)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(
                ERROR_NOTIFICATION_ID,
                notification
            )
        }
    }
}
