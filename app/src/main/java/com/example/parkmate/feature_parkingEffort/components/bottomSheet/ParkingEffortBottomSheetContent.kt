package com.example.parkmate.feature_parkingEffort.components.bottomSheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.parkingEffort.RankedParkingZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingEffortBottomSheetContent(
    zone: ParkingZone?,
    topZones: List<RankedParkingZone>,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        stringResource(id = R.string.pe_tab_leaderboard),
        stringResource(id = R.string.pe_tab_score)
    )

    LaunchedEffect(zone) {
        if (zone != null) {
            selectedTabIndex = 1
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when (selectedTabIndex) {
                0 -> ParkingEffortScoreSection(zone = zone, isConnected = isConnected)
                1 -> ParkingEffortLeaderboard(topZones = topZones)
            }
        }
    }
}