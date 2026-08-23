package com.example.parkmate.feature_vehiclesManager.components.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle

@Composable
fun VehicleCard(
    parkedVehicle: ParkedVehicle,
    isGridView: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isGridView) {
        VehicleGridCard(parkedVehicle, onEditClick, onDeleteClick, modifier)
    } else {
        VehicleListCard(parkedVehicle, onEditClick, onDeleteClick, modifier)
    }
}



