package com.example.parkmate.workManager

import android.content.Context
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.parkmate.notifications.NotificationsHelper

/**
 * Classe base per tutti i Worker che necessitano di girare in foreground
 * garantendo che il sistema operativo non li killi prematuramente.
 */
abstract class BaseForegroundWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    /**
     * Crea e restituisce le informazioni per far girare il Worker in primo piano.
     * @param notificationId L'ID univoco della notifica
     * @param title Il titolo della notifica
     * @param content Il testo della notifica
     * @param serviceType Il tipo di servizio (es. ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
     */
    protected fun createForegroundInfo(
        notificationId: Int,
        title: String,
        content: String,
        serviceType: Int = 0
    ): ForegroundInfo {
        NotificationsHelper.createNotificationChannel(appContext)
        val notification = NotificationsHelper.buildWorkerNotification(appContext, title, content)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, serviceType)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }
}