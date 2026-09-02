package com.example.parkmate.feature_home.presentation.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingSessionWithVehicleInfo
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.Position
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingSessionDetails(
    item: ParkingSessionWithVehicleInfo,
    onDismiss: () -> Unit,
    onDeleteSession: (ParkingSession) -> Unit,
    onConcludeSession: (ParkingSession) -> Unit,
    onOpenMapClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = if (item.session.photoUri == null) {
            { BottomSheetDefaults.DragHandle() }
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (item.session.photoUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = item.session.photoUri,
                        contentDescription = "Parking session photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.vehicleName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = {
                            Text(
                                text = if (item.session.isActive) stringResource(R.string.active) else stringResource(R.string.concluded),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            disabledContainerColor = if (item.session.isActive) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            disabledLabelColor = if (item.session.isActive) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    )
                }

                HorizontalDivider()

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)){
                    Text(
                        text = "VEHICLE TYPE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${item.vehicleType}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.home_rate_details),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    when (item.session.parkingType) {
                        ParkingType.HOURLY_PAID -> {
                            Text(
                                text = stringResource(R.string.home_hourly_rate)+": €${item.session.hourlyRate ?: "0.00"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        ParkingType.FIXED_TICKET -> {
                            Text(
                                text = stringResource(R.string.home_fixed_cost)+": €${item.session.fixedCost ?: "0.00"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            item.session.expiryTime?.let { expiry ->
                                Text(
                                    text = stringResource(R.string.home_expiry_date)+": ${formatDuration(expiry)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        ParkingType.FREE -> {
                            Text(
                                text = stringResource(R.string.home_free_parking),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    item.session.finalCost?.let { cost ->
                        val costFormatted = String.format(java.util.Locale.getDefault(), "%.2f", item.session.finalCost)
                        val finalText = "${stringResource(R.string.home_final_cost)}: €$costFormatted"

                        Text(
                            text = finalText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.home_time),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(stringResource(R.string.start_date)+": ${formatDuration(item.session.startTime)}")
                    item.session.endTime?.let { end ->
                        Text(stringResource(R.string.end_date)+": ${formatDuration(end)}")
                    }
                }

                if (item.session.isActive) {
                    RateDetailsCard(
                        item.session.startTime,
                        item.session.hourlyRate,
                        item.session.expiryTime
                    )
                }

                OutlinedCard(
                    onClick = { onOpenMapClick(item.session.position) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.home_parking_location),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${item.session.position.latitude}, ${item.session.position.longitude}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item.session.note?.takeIf { it.isNotBlank() }?.let { note ->
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_note),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.delete))
                }

                if (item.session.isActive) {
                    Button(
                        onClick = {
                            onConcludeSession(item.session)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Conclude Session")
                    }
                }
            }
        }
    }


    if (showDeleteDialog){
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(stringResource(R.string.home_delete_alert))
            },
            text = {
                Text(stringResource(R.string.home_delete_alert_desc))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteSession (item.session)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.undo))
                }
            },
            modifier = modifier
        )
    }
}

@Composable
fun RateDetailsCard(
    startTime: Long,
    hourlyRate: Double?,
    expiryTime: Long?,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Update currentTime every 1 sec
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    OutlinedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Session Info",
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                if (hourlyRate != null) {
                    val elapsedMillis = max(0L, currentTime - startTime)
                    val elapsedHours = elapsedMillis.toDouble() / (1000 * 60 * 60)
                    val accumulatedCost = elapsedHours * hourlyRate

                    Text(
                        text = stringResource(R.string.home_elapsed_time)+": ${formatDuration(elapsedMillis)}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = stringResource(R.string.home_accumulated_cost)+": ${String.format(Locale.getDefault(), "%.2f €", accumulatedCost)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (expiryTime != null) {
                    val remainingMillis = max(0L, expiryTime - currentTime)
                    val isExpired = currentTime >= expiryTime

                    if (isExpired) {
                        Text(
                            text = stringResource(R.string.home_expired),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.home_expires)+": ${formatDuration(remainingMillis)}",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                } else {
                    val elapsedMillis = max(0L, currentTime - startTime)

                    Text(
                        text = stringResource(R.string.home_elapsed_time)+": ${formatDuration(elapsedMillis)}",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

// Utility function
private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}