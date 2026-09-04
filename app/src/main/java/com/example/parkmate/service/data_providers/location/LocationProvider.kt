package com.example.parkmate.service.data_providers.location

import android.annotation.SuppressLint
import android.content.Context
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.permissions.RouteTrackingPermissions.isLocationEnabled
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

sealed class LocationException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class LocationDisabledException : LocationException("Servizi di localizzazione o GPS disattivati o Google Play Services non disponibili.")
    class LocationUnavailableException(cause: Throwable? = null) : LocationException("Impossibile rilevare la posizione attuale dopo i tentativi di retry.", cause)
}

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    private var cachedPosition: Position? = null
    private var lastCacheTimestamp: Long = 0L
    private val cacheValidityMillis: Long = 3_000L

    @SuppressLint("MissingPermission")
    suspend fun getCurrentPosition(
        maxRetries: Int = 3,
        retryDelayMillis: Long = 1_000L,
        timeoutMillis: Long = 4_000L
    ): Position = withContext(Dispatchers.IO) {

        val currentTime = System.currentTimeMillis()
        cachedPosition?.let { pos ->
            if (currentTime - lastCacheTimestamp < cacheValidityMillis) {
                return@withContext pos
            }
        }

        if (!isLocationEnabled(context)) {
            throw LocationException.LocationDisabledException()
        }

        var lastException: Exception? = null

        for (attempt in 1..maxRetries) {
            if (!isLocationEnabled(context)) {
                throw LocationException.LocationDisabledException()
            }

            val cancellationTokenSource = CancellationTokenSource()

            try {
                val location = withTimeoutOrNull(timeoutMillis) {
                    fusedLocationClient
                        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                        .await()
                } ?: fusedLocationClient.lastLocation.await()

                if (location != null) {
                    val position = Position(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = if (location.hasAltitude()) location.altitude else null
                    )

                    cachedPosition = position
                    lastCacheTimestamp = System.currentTimeMillis()

                    return@withContext position
                }
            } catch (e: Exception) {
                lastException = e
            } finally {
                cancellationTokenSource.cancel()
            }

            if (attempt < maxRetries) {
                delay(retryDelayMillis)
            }
        }

        throw LocationException.LocationUnavailableException(
            cause = lastException ?: Exception("Nessun fix GPS ottenuto dopo $maxRetries tentativi.")
        )
    }
}