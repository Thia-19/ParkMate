package com.example.parkmate.feature_vehiclesManager.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle
import com.example.parkmate.feature_vehiclesManager.components.ActionIconButton
import com.example.parkmate.feature_vehiclesManager.components.StatusPill

@Composable
fun VehicleListCard(
    parkedVehicle: ParkedVehicle,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                StatusPill(isParked = parkedVehicle.isParked)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = parkedVehicle.vehicle.name,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = parkedVehicle.vehicle.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionIconButton(
                        icon = Icons.Default.Edit,
                        onClick = onEditClick,
                        contentDescription = "Edit vehicle"
                    )
                    ActionIconButton(
                        icon = Icons.Default.Delete,
                        onClick = onDeleteClick,
                        contentDescription = "Delete vehicle"
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(width = 100.dp, height = 130.dp)
            ) {
                if (parkedVehicle.vehicle.imageUri != null) {
                    AsyncImage(
                        model = parkedVehicle.vehicle.imageUri,
                        contentDescription = "Image : ${parkedVehicle.vehicle.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}