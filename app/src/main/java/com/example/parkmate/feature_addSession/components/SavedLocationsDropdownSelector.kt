package com.example.parkmate.feature_addSession.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.parkmate.core.domain.models.SavedLocation
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocationsDropdownSelector(
    locations: List<SavedLocation>,
    selectedLocationId: Int?,
    onLocationSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
){
    var expanded by remember { mutableStateOf(false) }
    val selectedLocation = locations.find { it.id == selectedLocationId }
    val textToShow = selectedLocation?.name ?: "Current Position (GPS)"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded},
        modifier = modifier
    ) {
        OutlinedTextField(
            value = textToShow,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Saved Location") },
            placeholder = { Text("Choose a Saved Location...") },
            leadingIcon = {
                Icon(
                    imageVector = if (selectedLocationId == null) Icons.Default.MyLocation else Icons.Default.Place,
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
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Current Position (GPS)") },
                onClick = {
                    onLocationSelected(null)
                    expanded = false
                }
            )

            if (locations.isNotEmpty()) {
                HorizontalDivider()
            }

            locations.forEach { location ->
                DropdownMenuItem(
                    text = { Text(text = location.name) },
                    onClick = {
                        onLocationSelected(location.id)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}