package com.example.parkmate.feature_vehiclesManager.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.vehicle.VehicleType
import com.example.parkmate.feature_vehiclesManager.util.labelRes
import com.example.parkmate.feature_vehiclesManager.viewModel.util.VehicleFilterState
import com.example.parkmate.feature_vehiclesManager.viewModel.util.VehicleStatusFilter

@Composable
fun VehicleFilters(
    filterState: VehicleFilterState,
    onStatusFilterSelected: (VehicleStatusFilter) -> Unit,
    onTypeFilterSelected: (VehicleType?) -> Unit,
    modifier: Modifier = Modifier
) {
    var typeMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ElevatedFilterChip(
            selected = filterState.status == VehicleStatusFilter.AVAILABLE,
            onClick = {
                onStatusFilterSelected(filterState.status.toggled(VehicleStatusFilter.AVAILABLE))
            },
            label = { Text(stringResource(R.string.status_available)) },
            colors = FilterChipDefaults.elevatedFilterChipColors()
        )

        ElevatedFilterChip(
            selected = filterState.status == VehicleStatusFilter.PARKED,
            onClick = {
                onStatusFilterSelected(filterState.status.toggled(VehicleStatusFilter.PARKED))
            },
            label = { Text(stringResource(R.string.status_parked)) },
            colors = FilterChipDefaults.elevatedFilterChipColors()
        )

        Box {
            ElevatedFilterChip(
                selected = filterState.type != null,
                onClick = { typeMenuExpanded = true },
                label = {
                    val labelText = filterState.type?.let { stringResource(it.labelRes) }
                        ?: stringResource(R.string.all_types)
                    Text(labelText)
                }
            )

            DropdownMenu(
                expanded = typeMenuExpanded,
                onDismissRequest = { typeMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.all_types)) },
                    onClick = {
                        onTypeFilterSelected(null)
                        typeMenuExpanded = false
                    },
                    trailingIcon = {
                        if (filterState.type == null) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    }
                )

                VehicleType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(stringResource(type.labelRes)) },
                        onClick = {
                            onTypeFilterSelected(type)
                            typeMenuExpanded = false
                        },
                        trailingIcon = {
                            if (filterState.type == type) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun VehicleStatusFilter.toggled(target: VehicleStatusFilter): VehicleStatusFilter =
    if (this == target) VehicleStatusFilter.ALL else target