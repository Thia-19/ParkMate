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
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import androidx.compose.ui.res.stringResource
import com.example.parkmate.core.domain.models.labelRes
import com.example.parkmate.core.domain.utils.formatTimestamp

@Composable
fun ParkingSessionCard(
    session: ParkingSession,
    onClick: (ParkingSession) -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        onClick = { onClick(session) },

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
                    text = "Vehicle ID: ${session.vehicleId}",
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
                text = "Start date: ${formatTimestamp(session.startTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Position: ${session.position.latitude}, ${session.position.longitude}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Parking type: ${stringResource(id = session.parkingType.labelRes)}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Opzionale, mi sembra troppo in una sola schermata
            when (session.parkingType) {
                ParkingType.HOURLY_PAID -> {
                    session.hourlyRate?.let {
                        Text(
                            text = "Hourly Rate: €%.2f/h".format(it),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                ParkingType.FIXED_TICKET -> {
                    session.fixedCost?.let {
                        Text(
                            text = "Fixed cost: €%.2f".format(it),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                ParkingType.FREE -> { /* Nessun dettaglio extra di costo */ }
            }

            // Mostra costo finale se presente (es. sosta terminata)
            session.finalCost?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total: €%.2f".format(it),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/*Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.parkingType.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                session.finalCost?.let {
                    Text(
                        text = "€ %.2f".format(it),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

 */