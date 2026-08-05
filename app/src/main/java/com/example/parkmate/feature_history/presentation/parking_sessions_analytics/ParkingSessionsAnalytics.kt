package com.example.parkmate.feature_history.presentation.parking_sessions_analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmate.feature_history.presentation.HistoryEvent
import com.example.parkmate.feature_history.presentation.HistoryState
import com.example.parkmate.feature_history.presentation.parking_sessions_analytics.components.TotExpensesBarChart
import com.example.parkmate.feature_history.presentation.util.aggregateByMonth

@Composable
fun ParkingSessionsAnalytics(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthlyData = remember(state.allSession){
        state.allSession.aggregateByMonth()
    }

    val totalSpent = remember (state.allSession){
        state.allSession.filter { !it.isActive }.sumOf { it.finalCost ?: 0.0 }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Total historical spent amount",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "€ ${String.format("%.2f", totalSpent)}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Monthly parking expenses",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                if (monthlyData.isEmpty()) {
                    Text(
                        text = "No completed parking sessions recorded.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    TotExpensesBarChart(expenses = monthlyData)
                }
            }
        }
    }
}