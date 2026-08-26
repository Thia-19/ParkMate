package com.example.parkmate.feature_savedLocations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.SavedLocation
import com.example.parkmate.feature_savedLocations.SavedLocationsEvent

@Composable
fun SavedLocationContent(
    locations: List<SavedLocation>,
    isLoading: Boolean,
    onEvent: (SavedLocationsEvent) -> Unit,
    modifier: Modifier = Modifier,
){
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (locations.isEmpty()){

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "info"
                    )

                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        text = stringResource(R.string.saved_locations_alt),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.padding(6.dp))

                Text(
                    text = stringResource(id = R.string.saved_locations_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = stringResource(R.string.saved_locations_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        itemsIndexed(
            items = locations,
            key = { _, location -> location.id }
        ) { index, location ->
            SavedLocationCard(
                location = location,
                onClick = { /* TODO */ },
                onEvent = onEvent
            )
        }
    }
}