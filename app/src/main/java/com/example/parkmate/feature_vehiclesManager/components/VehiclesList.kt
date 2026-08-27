package com.example.parkmate.feature_vehiclesManager.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle

@Composable
fun VehiclesList(
    vehicles: List<ParkedVehicle>,
    onEditVehicle: (ParkedVehicle) -> Unit,
    onDeleteVehicle: (ParkedVehicle) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = vehicles,
            key = { it.vehicle.id }
        ) { parkedVehicle ->
            VehicleListCard(
                parkedVehicle = parkedVehicle,
                onEditClick = { onEditVehicle(parkedVehicle) },
                onDeleteClick = { onDeleteVehicle(parkedVehicle) }
            )
        }
    }
}