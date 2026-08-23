package com.example.parkmate.feature_history.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.parkmate.core.domain.models.ParkingType
import com.example.parkmate.core.domain.models.vehicle.Vehicle
import com.example.parkmate.core.domain.models.labelRes
import com.example.parkmate.feature_history.presentation.HistoryEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    vehicles: List<Vehicle>,
    currentVehicleId: Int?,
    currentParkingType: ParkingType?,
    startDate: Long?,
    endDate: Long?,
    onEvent: (HistoryEvent) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
){
    val sheetState = rememberModalBottomSheetState()

    var showDateRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startDate,
        initialSelectedEndDateMillis = endDate
    )

    // Formatting the date
    val formattedRange = remember(startDate, endDate) {
        if (startDate != null && endDate != null) {
            val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            "${formatter.format(Date(startDate))} - ${formatter.format(Date(endDate))}"
        } else if (startDate != null) {
            val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            "From ${formatter.format(Date(startDate))}"
        } else {
            "All ranges"
        }
    }

    var isDropdownExpanded by remember { mutableStateOf(false) }
    val selectedVehicle = remember(currentVehicleId, vehicles) {
        vehicles.find { it.id == currentVehicleId }
    }

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

            OutlinedButton(
                onClick = { showDateRangePicker = true},
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = "Range: ${formattedRange}")
            }

            // Vehicle filter
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { isDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.FireTruck,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(text = "Vehicles: ${selectedVehicle?.name ?: "All"}")
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false}
                ) {
                    DropdownMenuItem(
                        text = { Text("All vehicles") },
                        onClick = {
                            onEvent(HistoryEvent.FilterByVehicle(vehicleId = null))
                            isDropdownExpanded = false
                        }
                    )

                    // Opzione 2: Un elemento per ogni veicolo reale
                    vehicles.forEach { vehicle ->
                        DropdownMenuItem(
                            text = { Text("${vehicle.name} (${vehicle.id})") },
                            onClick = {
                                onEvent(HistoryEvent.FilterByVehicle(vehicleId = vehicle.id))
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // ParkingType filter
            // Usa LazyRow per la massima efficienza
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp) // Spazio piacevole ai bordi
            ) {
                items(ParkingType.entries.toList()) { type ->
                    FilterChip(
                        selected = currentParkingType == type,
                        onClick = { onEvent(HistoryEvent.FilterByParkType(type)) },
                        label = {
                            Text(
                                text = stringResource(id = type.labelRes),
                                maxLines = 1 // Garantisce che il testo non vada mai a capo
                            )
                        },
                        leadingIcon = if (currentParkingType == type) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null
                    )
                }
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

        if (showDateRangePicker){
            DatePickerDialog(
                onDismissRequest = { showDateRangePicker = false},
                confirmButton = {
                    TextButton(
                        onClick = {
                            val start = dateRangePickerState.selectedStartDateMillis
                            val end = dateRangePickerState.selectedEndDateMillis

                            onEvent(
                                HistoryEvent.FilterByDateRange(
                                    startDate = start,
                                    endDate = end
                                )
                            )
                            showDateRangePicker = false
                        },
                        enabled = dateRangePickerState.selectedEndDateMillis != null
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {showDateRangePicker = false}) {
                        Text("Undo")// annulla
                    }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    title = {
                        Text(
                            text = "Select Range",
                            modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                )
            }
        }
    }

}