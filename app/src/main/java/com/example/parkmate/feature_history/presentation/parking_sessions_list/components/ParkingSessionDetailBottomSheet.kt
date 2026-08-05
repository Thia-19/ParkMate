package com.example.parkmate.feature_history.presentation.parking_sessions_list.components

import android.R.attr.label
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parkmate.core.domain.models.ParkingSession
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.labelRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingSessionDetailBottomSheet(
    session: ParkingSession,
    onDismissRequest: () -> Unit,
    onDeleteClick: (ParkingSession) -> Unit,
    modifier: Modifier = Modifier
){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    IconButton(
                        onClick = { onDeleteClick(session) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Session",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                }

            HorizontalDivider()

            DetailRow(label = "Vehicle ID", value = "#${session.vehicleId}")

            DetailRow(
                label = "Parking Type",
                value = stringResource(id = session.parkingType.labelRes)
            )

            DetailRow(
                label = "Start date",
                value = formatTimestamp(session.startTime)
            )

            session.endTime?.let {
                DetailRow(
                    label = "End Date",
                    value = formatTimestamp(it)
                )
            }

            session.expiryTime?.let {
                DetailRow(
                    label = "Ticket expiry",
                    value = formatTimestamp(it)
                )
            }

            DetailRow(
                label = "Position",
                value = "${session.position.latitude}, ${session.position.longitude}"
            )

            when (session.parkingType) {
                ParkingType.HOURLY_PAID -> {
                    session.hourlyRate?.let {
                        DetailRow(label = "Hourly rate", value = "€ %.2f/h".format(it))
                    }
                }
                ParkingType.FIXED_TICKET -> {
                    session.fixedCost?.let {
                        DetailRow(label = "Fixed cost", value = "€ %.2f".format(it))
                    }
                }
                ParkingType.FREE -> {
                    DetailRow(label = "Rate", value = "Free")
                }
            }

            session.finalCost?.let {
                DetailRow(
                    label = "Total",
                    value = "€ %.2f".format(it),
                    isHighlight = true
                )
            }

            session.note?.takeIf { it.isNotBlank() }?.let { note ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Note:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            session.photoUri?.let { uri ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Location photo:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    // Utilizza Coil per caricare l'immagine da URI
                    AsyncImage(
                        model = uri,
                        contentDescription = "Parking Location Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ma se lo mettessi in util?
fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}