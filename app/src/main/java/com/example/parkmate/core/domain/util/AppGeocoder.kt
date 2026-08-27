package com.example.parkmate.core.domain.util

import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppGeocoder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(longitude: Double, latitude: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())

                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0) ?: "Indirizzo sconosciuto"
                } else {
                    "Indirizzo non trovato"
                }

            } catch (e: Exception) {
                "Coordinate: ${String.format(Locale.US, "%.4f", latitude)}, ${String.format(Locale.US, "%.4f", longitude)}"
            }
        }
    }
}