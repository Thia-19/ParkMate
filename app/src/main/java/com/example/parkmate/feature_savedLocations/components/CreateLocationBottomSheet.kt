package com.example.parkmate.feature_savedLocations.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkmate.core.domain.models.MarkerIconType
import com.example.parkmate.core.domain.models.Position
import com.example.parkmate.core.domain.models.SavedLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLocationBottomSheet(
    locationToEdit: SavedLocation? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, icon: MarkerIconType, position: Position) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(locationToEdit?.name ?: "") }
    var icon by rememberSaveable { mutableStateOf(locationToEdit?.icon ?: MarkerIconType.HOME) }
    var position by remember { mutableStateOf<Position?>(locationToEdit?.position) }

    val initialLatLng = remember(locationToEdit) {
        locationToEdit?.position?.let { LatLng(it.latitude, it.longitude) }
            ?: LatLng(44.4939, 11.3428)
    }

    val cameraPositionState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(
            initialLatLng,
            if (locationToEdit != null) 15f else 11f
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = if (locationToEdit != null) "Edit Location" else "Create Location",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField (
                value = name,
                onValueChange = { name = it },
                label = {Text("Name (e.g. Home, Work)")},
                minLines = 1,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            Text(
                text = "Tap on map to set position",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        position = Position(
                            latitude = latLng.latitude,
                            longitude = latLng.longitude
                        )
                    }
                ){
                    position?.let { pos ->
                        Marker (
                            state = MarkerState(position = LatLng(pos.latitude, pos.longitude)),
                            title = name.ifBlank { "New Location" }
                        )
                    }
                }
            }

            Text(
                text = "Select Icon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            MarkerIconTypeSelector(
                selectedType = icon,
                onTypeSelected = { selected -> icon = selected}
            )

            HorizontalDivider()

            Button (
                onClick = {
                    position?.let { pos ->
                        onSave(name, icon, pos)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank() && position != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Location")
            }
        }

    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MarkerIconTypeSelector(
    selectedType: MarkerIconType,
    onTypeSelected: (MarkerIconType) -> Unit,
    modifier: Modifier = Modifier
){
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 3
    ) {
        MarkerIconType.entries.forEach { type ->
            val isSelected = (type == selectedType)

            Surface(
                selected = isSelected,
                onClick = { onTypeSelected(type) },
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                else null,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = type.name,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}