package com.example.parkmate.core.domain.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.example.parkmate.core.domain.util.observers.ConnectivityObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AppGeocoder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val connectivityObserver: ConnectivityObserver
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): String {
        val formattedCoordinates = String.format(Locale.getDefault(), "%.4f, %.4f", latitude, longitude)

        if (!connectivityObserver.isCurrentlyConnected()) {
            return formattedCoordinates
        }

        val geocoder = Geocoder(context, Locale.getDefault())

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            val addressLine = addresses.firstOrNull()?.getAddressLine(0)
                            continuation.resume(addressLine ?: formattedCoordinates)
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resume(formattedCoordinates)
                        }
                    })
                }
            } else {
                withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    addresses?.firstOrNull()?.getAddressLine(0) ?: formattedCoordinates
                }
            }
        } catch (e: Exception) {
            formattedCoordinates
        }
    }

    suspend operator fun invoke(longitude: Double, latitude: Double, isLongitudeFirst: Boolean = true): String {
        return invoke(latitude = latitude, longitude = longitude)
    }
}