package com.example.parkmate.feature_parkingEffort.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.parkmate.R

@Composable
fun DestinationConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(onClick = onConfirmRequest) {
                Text(stringResource(id = R.string.pe_dialog_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.pe_dialog_no))
            }
        },
        icon = {
            Icon(imageVector = Icons.Default.Place, contentDescription = null)
        },
        title = { Text(stringResource(id = R.string.pe_dialog_dest_title)) },
        text = { Text(stringResource(id = R.string.pe_dialog_dest_message)) }
    )
}