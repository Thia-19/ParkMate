package com.example.parkmate.feature_history.presentation.parking_sessions_map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.parkmate.feature_history.domain.models.TimeRange

@Composable
fun TimeRangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
){
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        TimeRange.entries.forEach { range ->
            FilterChip(
                selected = (range == selectedRange),
                onClick = { onRangeSelected(range) },
                label = { Text(text = stringResource(id = range.labelResId)) },
            )
        }
    }
}