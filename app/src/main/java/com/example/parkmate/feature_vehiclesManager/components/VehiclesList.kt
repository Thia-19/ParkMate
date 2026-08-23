package com.example.parkmate.feature_vehiclesManager.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle
import com.example.parkmate.feature_vehiclesManager.components.cards.VehicleCard

@Composable
fun VehiclesList(
    vehicles: List<ParkedVehicle>,
    isGridView: Boolean,
    onEditVehicle: (ParkedVehicle) -> Unit,
    onDeleteVehicle: (ParkedVehicle) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isGridView) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = vehicles,
                key = { it.vehicle.id }
            ) { parkedVehicle ->
                VehicleCard(
                    parkedVehicle = parkedVehicle,
                    isGridView = true,
                    onEditClick = { onEditVehicle(parkedVehicle) },
                    onDeleteClick = { onDeleteVehicle(parkedVehicle) }
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = vehicles,
                key = { it.vehicle.id }
            ) { parkedVehicle ->
                VehicleCard(
                    parkedVehicle = parkedVehicle,
                    isGridView = false,
                    onEditClick = { onEditVehicle(parkedVehicle) },
                    onDeleteClick = { onDeleteVehicle(parkedVehicle) }
                )
            }
        }
    }
}