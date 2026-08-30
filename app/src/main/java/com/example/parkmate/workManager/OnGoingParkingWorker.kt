package com.example.parkmate.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parkmate.core.domain.repository.ParkingSessionRepository
import com.example.parkmate.notifications.NotificationsHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class OngoingParkingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val parkingRepository: ParkingSessionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sessionId = inputData.getInt(EXTRA_SESSION_ID, -1)
        if (sessionId == -1) {
            return Result.failure()
        }

        val item = parkingRepository.getParkingSessionWithVehicleById(sessionId)

        if (item == null || !item.session.isActive) {
            NotificationsHelper.cancelOngoingParkingNotification(context)
            return Result.success()
        }

        val vehicleName = item.vehicleName
        val (title, content) = item.session.getOngoingReminderMessage(context)

        val titleWithVehicle = "$title ($vehicleName)"
        NotificationsHelper.showOngoingParkingNotification(
            context = context,
            title = titleWithVehicle,
            contentText = content
        )

        return Result.success()
    }

    companion object {
        const val EXTRA_SESSION_ID = "extra_session_id"
        const val WORK_TAG_PREFIX = "ongoing_parking_work_"
    }
}