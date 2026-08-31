package com.example.parkmate.feature_history.presentation.parking_sessions_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.parkmate.R
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
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.history_vehicle)+": ${item.vehicleName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.concluded),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.start_date)+": ${formatTimestamp(item.session.startTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.end_date)+": ${formatTimestamp(item.session.endTime!!)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.parking_type)+": ${stringResource(id = item.session.parkingType.labelRes)}",
                style = MaterialTheme.typography.bodyMedium
            )

            item.session.finalCost?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.total)+": €%.2f".format(it),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
