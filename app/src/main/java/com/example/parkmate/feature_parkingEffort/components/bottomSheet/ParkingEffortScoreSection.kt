package com.example.parkmate.feature_parkingEffort.components.bottomSheet

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

@SuppressLint("DefaultLocale")
@Composable
fun ParkingEffortScoreSection(
    zone: ParkingZone?,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    if (zone == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.pe_score_no_zone_selected),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val context = LocalContext.current
    val loadingAddressText = stringResource(id = R.string.pe_score_address_loading)
    val unknownAddressText = stringResource(id = R.string.pe_address_unknown)
    val notFoundAddressText = stringResource(id = R.string.pe_address_not_found)

    var zoneAddress by remember(zone.id) { mutableStateOf(loadingAddressText) }

    LaunchedEffect(zone.id, isConnected) {
        zoneAddress = resolveAddress(
            context = context,
            latitude = zone.centerPosition.latitude,
            longitude = zone.centerPosition.longitude,
            isConnected = isConnected,
            unknownText = unknownAddressText,
            notFoundText = notFoundAddressText
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location Icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = zoneAddress,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Score star",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            id = R.string.pe_score_format,
                            String.format(Locale.getDefault(), "%.1f", zone.averageScore)
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.pe_score_destinations_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        val destinations = zone.recentDestinations

        if (destinations.isEmpty()) {
            Text(
                text = stringResource(id = R.string.pe_score_no_destinations),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(
                    items = destinations,
                    key = { _, dest -> "${dest.latitude}_${dest.longitude}" }
                ) { index, dest ->
                    DestinationItem(
                        index = index + 1,
                        latitude = dest.latitude,
                        longitude = dest.longitude,
                        isConnected = isConnected
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinationItem(
    index: Int,
    latitude: Double,
    longitude: Double,
    isConnected: Boolean
) {
    val context = LocalContext.current
    val loadingText = stringResource(id = R.string.pe_score_address_loading)
    val unknownText = stringResource(id = R.string.pe_address_unknown)
    val notFoundText = stringResource(id = R.string.pe_address_not_found)

    var address by remember(latitude, longitude) { mutableStateOf(loadingText) }

    LaunchedEffect(latitude, longitude, isConnected) {
        address = resolveAddress(
            context = context,
            latitude = latitude,
            longitude = longitude,
            isConnected = isConnected,
            unknownText = unknownText,
            notFoundText = notFoundText
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = stringResource(id = R.string.pe_score_destination_item_title, index),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private suspend fun resolveAddress(
    context: Context,
    latitude: Double,
    longitude: Double,
    isConnected: Boolean,
    unknownText: String,
    notFoundText: String
): String {
    val fallbackText = "Coordinate: ${String.format(Locale.getDefault(), "%.4f", latitude)}, ${String.format(Locale.getDefault(), "%.4f", longitude)}"

    if (!isConnected) {
        return fallbackText
    }

    val geocoder = Geocoder(context, Locale.getDefault())

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val addressLine = addresses[0].getAddressLine(0) ?: unknownText
                            continuation.resume(addressLine)
                        } else {
                            continuation.resume(notFoundText)
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        continuation.resume(fallbackText)
                    }
                })
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0) ?: unknownText
                } else {
                    notFoundText
                }
            }
        }
    } catch (e: Exception) {
        fallbackText
    }
}