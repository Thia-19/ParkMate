package com.example.parkmate.feature_home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.vehicle.VehicleType
import com.example.parkmate.core.domain.util.formatTimestamp

@Composable
fun ParkingSessionCardHome(
    item: ParkingSessionWithVehicleInfo,
    onClick: (ParkingSession) -> Unit,
    modifier: Modifier = Modifier
){
    val isExpired = item.session.parkingType == ParkingType.FIXED_TICKET &&
            item.session.expiryTime != null &&
            item.session.expiryTime < System.currentTimeMillis()

    val containerColor = if (isExpired) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val contentColor = if (isExpired) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val vehicleIconContainer = if (isExpired) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val vehicleIconTint = if (isExpired) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val badgeContainer = if (isExpired) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val badgeTint = if (isExpired) {
        MaterialTheme.colorScheme.onError
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        onClick = { onClick(item.session) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = vehicleIconContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val icon = when (item.vehicleType) {
                        VehicleType.CAR -> Icons.Default.DirectionsCar
                        VehicleType.MOTORCYCLE -> Icons.Default.TwoWheeler
                        VehicleType.BICYCLE -> Icons.Default.PedalBike
                        VehicleType.OTHER -> Icons.Default.ElectricScooter
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = item.vehicleType.name,
                        tint = vehicleIconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.vehicleName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (isExpired) {
                            Surface(
                                color = MaterialTheme.colorScheme.error,
                                shape = MaterialTheme.shapes.extraSmall,
                            ) {
                                Text(
                                    text = stringResource(R.string.home_expired_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onError,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (!isExpired) {
                        Surface(
                            shape = CircleShape,
                            color = badgeContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val parkingIcon = when (item.session.parkingType) {
                                    ParkingType.FREE -> Icons.Default.MoneyOff
                                    ParkingType.HOURLY_PAID, ParkingType.FIXED_TICKET -> Icons.Default.AttachMoney
                                }
                                Icon(
                                    imageVector = parkingIcon,
                                    contentDescription = item.session.parkingType.name,
                                    tint = badgeTint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.start_date) + ": ${formatTimestamp(item.session.startTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}