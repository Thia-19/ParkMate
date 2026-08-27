package com.example.parkmate.feature_vehiclesManager.components.wrappers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@Composable
fun SwipeToDeleteWrapper(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    content: @Composable RowScope.() -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        onDismiss = { direction ->
            if (direction == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
        },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Elimina veicolo",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = content
    )
}
