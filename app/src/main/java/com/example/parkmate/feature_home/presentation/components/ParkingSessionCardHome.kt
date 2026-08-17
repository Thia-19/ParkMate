package com.example.parkmate.feature_home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.util.formatTimestamp

@Composable
fun ParkingSessionCardHome(
    session: ParkingSession,
    onClick: (ParkingSession) -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        onClick = { onClick(session) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vehicle ID: ${session.vehicleId}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    when (session.parkingType){
                        ParkingType.FREE -> {
                            Icon(
                                imageVector = Icons.Default.MoneyOff,
                                contentDescription = "Free"
                            )
                        }
                        ParkingType.HOURLY_PAID -> {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "HourlyPaid"
                            )
                        }
                        ParkingType.FIXED_TICKET -> {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "FixedTicket"
                            )
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start date: ${formatTimestamp(session.startTime)}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "Position: ${session.position.latitude}, ${session.position.longitude}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            }
    }
}