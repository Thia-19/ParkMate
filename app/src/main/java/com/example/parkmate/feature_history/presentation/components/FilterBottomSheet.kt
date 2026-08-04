package com.example.parkmate.feature_history.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.feature_history.presentation.HistoryEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentVehicleId: Int?,
    currentParkingType: ParkingType?,
    currentDateRange: Long?,
    onEvent: (HistoryEvent) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
){
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Filter Sessions",
                style = MaterialTheme.typography.titleLarge
            )

            // Vehicle filter
            Button(onClick = {
                onEvent(HistoryEvent.FilterByVehicle(vehicleId = 123))
            }) {
                Text("Vehicle: ${currentVehicleId ?: "All"}")
            }

            // ParkingType filter
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ParkingType.entries.forEach { type ->
                    FilterChip(
                        selected = currentParkingType == type,
                        onClick = { onEvent(HistoryEvent.FilterByParkType(type))},
                        label = {Text(type.name)}
                    )
                }
            }

            // Date range filter
            Button(onClick = {
                onEvent(HistoryEvent.FilterByDateRange(System.currentTimeMillis()))
            }) {
                Text("Date Range: ${currentDateRange ?: "All"}")
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    onEvent(HistoryEvent.ResetFilters)
                }) {
                    Text("Reset filters")
                }

                Button(onClick = onDismissRequest) {
                    Text("Apply")
                }
            }
        }
    }

}