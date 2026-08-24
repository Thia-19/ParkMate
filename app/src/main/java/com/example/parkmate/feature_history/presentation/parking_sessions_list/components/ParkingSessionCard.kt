package com.example.parkmate.feature_history.presentation.parking_sessions_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmate.core.domain.models.ParkingType
import androidx.compose.ui.res.stringResource
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.labelRes
import com.example.parkmate.core.domain.util.formatTimestamp

@Composable
fun ParkingSessionCard(
    item: ParkingSessionWithVehicleInfo,
    onClick: (ParkingSessionWithVehicleInfo) -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        onClick = { onClick(item) },

        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vehicle Name: ${item.vehicleName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "CONCLUDED",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start date: ${formatTimestamp(item.session.startTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Position: ${item.session.position.latitude}, ${item.session.position.longitude}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Parking type: ${stringResource(id = item.session.parkingType.labelRes)}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Opzionale, mi sembra troppo in una sola schermata
            when (item.session.parkingType) {
                ParkingType.HOURLY_PAID -> {
                    item.session.hourlyRate?.let {
                        Text(
                            text = "Hourly Rate: €%.2f/h".format(it),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                ParkingType.FIXED_TICKET -> {
                    item.session.fixedCost?.let {
                        Text(
                            text = "Fixed cost: €%.2f".format(it),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                ParkingType.FREE -> {  }
            }

            item.session.finalCost?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total: €%.2f".format(it),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
