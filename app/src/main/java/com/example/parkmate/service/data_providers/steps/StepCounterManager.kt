package com.example.parkmate.service.data_providers.steps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepCounterManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensorManager: SensorManager
) {

    fun canReadStepCount(contextToUse: Context = context): Boolean {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                contextToUse,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (!hasPermission) return false

        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        return stepCounterSensor != null
    }

    fun getStepsCount(
        maxRetries: Long = 3L,
        retryDelayMillis: Long = 2_000L
    ): Flow<Int> = callbackFlow {
        if (!canReadStepCount()) {
            close(IllegalStateException("Impossibile accedere al contapassi"))
            return@callbackFlow
        }

        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            close(IllegalStateException("Sensore STEP_COUNTER non presente sull'hardware del dispositivo."))
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.firstOrNull()?.let { totalStepsFloat ->
                    val totalStepsSinceBoot = totalStepsFloat.toInt()
                    trySend(totalStepsSinceBoot)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val registered = sensorManager.registerListener(
            listener,
            stepSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        if (!registered) {
            close(IllegalStateException("Impossibile registrare il listener sul SensorManager."))
            return@callbackFlow
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
        .retryWhen { _, attempt ->
            if (attempt < maxRetries) {
                delay(retryDelayMillis)
                true
            } else {
                false
            }
        }
        .catch {
            emit(0)
        }
        .distinctUntilChanged()
}