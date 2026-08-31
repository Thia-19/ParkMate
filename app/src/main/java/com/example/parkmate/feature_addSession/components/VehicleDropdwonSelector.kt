package com.example.parkmate.feature_addSession.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.parkmate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDropdownSelector(
    vehicles: List<com.example.parkmate.core.domain.models.vehicle.Vehicle>,
    selectedVehicleId: Int?,
    onVehicleSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    var expanded by remember { mutableStateOf(false) }
    val selectedVehicle = vehicles.find { it.id == selectedVehicleId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded},
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedVehicle?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.add_session_select_vehicle)) },
            placeholder = { Text(stringResource(R.string.add_session_choose_a_vehicle)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false }
        ) {
            if (vehicles.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.add_session_no_saved_vehicle)) },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                vehicles.forEach { vehicle ->
                    DropdownMenuItem(
                        text = {
                            Text(text = vehicle.name)
                        },
                        onClick = {
                            onVehicleSelected(vehicle.id)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

