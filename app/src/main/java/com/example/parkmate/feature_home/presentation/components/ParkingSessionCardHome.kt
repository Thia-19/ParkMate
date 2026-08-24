package com.example.parkmate.feature_home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsCarFilled
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.SportsMotorsports
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    Card(
        onClick = { onClick(item.session) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        when(item.vehicleType) {
                            (VehicleType.CAR) -> {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = "car",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            (VehicleType.MOTORCYCLE) -> {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = "motorcycle",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            (VehicleType.BICYCLE) -> {
                                Icon(
                                    imageVector = Icons.Default.PedalBike,
                                    contentDescription = "motorcycle",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            (VehicleType.OTHER) -> {
                                Icon(
                                    imageVector = Icons.Default.ElectricScooter,
                                    contentDescription = "motorcycle",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Vehicle Name: ${item.vehicleName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    when (item.session.parkingType){
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
                text = "Start date: ${formatTimestamp(item.session.startTime)}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "Position: ${item.session.position.latitude}, ${item.session.position.longitude}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            }
    }
}