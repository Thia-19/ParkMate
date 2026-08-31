package com.example.parkmate.feature_history.presentation.parking_sessions_analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.feature_history.domain.models.ParkingMetrics
import java.util.Locale

@Composable
fun ParkingMetricsCard(
    metrics: ParkingMetrics,
){
    val parkingTypeLabel = metrics.preferredParkingType?.getLabel()?: "N/D"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.history_KPI),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                KpiItem(
                    icon = Icons.Default.AttachMoney,
                    title = stringResource(R.string.history_KPI_average_expense),
                    value = "€ ${"%.2f".format(Locale.getDefault(), metrics.avgCostPerSession)}",
                    modifier = Modifier.weight(1f)
                )
                KpiItem(
                    icon = Icons.Default.Schedule,
                    title = stringResource(R.string.history_KPI_average_duraion),
                    value = formatMinutesToHours(metrics.avgDurationMinutes),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                KpiItem(
                    icon = Icons.Default.DirectionsCar,
                    title = stringResource(R.string.history_KPI_vehicle),
                    value = metrics.mostUsedVehicle ?: "N/D",
                    modifier = Modifier.weight(1f)
                )
                KpiItem(
                    icon = Icons.Default.LocalParking,
                    title = stringResource(R.string.history_KPI_parking_type),
                    value = parkingTypeLabel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun KpiItem(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ParkingType.getLabel(): String {
    return when (this) {
        ParkingType.FREE -> stringResource(R.string.parking_type_free)
        ParkingType.HOURLY_PAID -> stringResource(R.string.parking_type_hourly)
        ParkingType.FIXED_TICKET -> stringResource(R.string.parking_type_fixed)
    }
}

private fun formatMinutesToHours(totalMinutes: Long): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}