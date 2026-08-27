package com.example.parkmate.feature_vehiclesManager.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parkmate.core.domain.models.vehicle.ParkedVehicle
import com.example.parkmate.feature_vehiclesManager.components.wrappers.SwipeToDeleteWrapper

@Composable
fun VehicleListCard(
    parkedVehicle: ParkedVehicle,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageSize = 88.dp
    val cardShape = RoundedCornerShape(28.dp)

    SwipeToDeleteWrapper(
        onDelete = onDeleteClick,
        shape = cardShape,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 8.dp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(imageSize)
                ) {
                    parkedVehicle.vehicle.imageUri?.let { imageUri ->
                        AsyncImage(
                            model = imageUri,
                            contentDescription = parkedVehicle.vehicle.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(imageSize)
                ) {
                    StatusPill(
                        isParked = parkedVehicle.isParked,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )

                    Text(
                        text = parkedVehicle.vehicle.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = 48.dp)
                    )

                    ActionIconButton(
                        icon = Icons.Default.Edit,
                        onClick = onEditClick,
                        contentDescription = "Modifica veicolo",
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}