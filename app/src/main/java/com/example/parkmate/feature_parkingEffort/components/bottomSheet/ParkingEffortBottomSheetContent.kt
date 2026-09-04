package com.example.parkmate.feature_parkingEffort.components.bottomSheet

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.parkmate.R
import com.example.parkmate.core.domain.models.parkingEffort.ParkingZone
import com.example.parkmate.core.domain.models.parkingEffort.RankedParkingZone

private enum class ParkingEffortTab(@StringRes val titleRes: Int) {
    LEADERBOARD(R.string.pe_tab_leaderboard),
    SCORE(R.string.pe_tab_score)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingEffortBottomSheetContent(
    zone: ParkingZone?,
    topZones: List<RankedParkingZone>,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ParkingEffortTab.LEADERBOARD) }

    LaunchedEffect(zone) {
        selectedTab = if (zone != null) ParkingEffortTab.SCORE else ParkingEffortTab.LEADERBOARD
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(
                    if (zone != null) R.string.pe_discover_effort
                    else R.string.pe_discover_best_parking_zones
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        SecondaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            ParkingEffortTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = stringResource(tab.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when (selectedTab) {
                ParkingEffortTab.LEADERBOARD -> ParkingEffortLeaderboard(topZones = topZones)
                ParkingEffortTab.SCORE -> ParkingEffortScoreSection(zone = zone, isConnected = isConnected)
            }
        }
    }
}