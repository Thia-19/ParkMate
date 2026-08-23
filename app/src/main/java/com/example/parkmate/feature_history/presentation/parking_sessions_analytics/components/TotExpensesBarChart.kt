package com.example.parkmate.feature_history.presentation.parking_sessions_analytics.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parkmate.feature_history.domain.models.MonthlyExpense
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.util.Locale

@Composable
fun TotExpensesBarChart(
    expenses: List<MonthlyExpense>,
    modifier: Modifier = Modifier
){
    if (expenses.isEmpty()) return

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(expenses) {
        modelProducer.runTransaction {
            columnSeries {
                series(expenses.map { it.totalCost })
            }
        }
    }

    val bottomAxisFormatter = remember(expenses) {
        CartesianValueFormatter { x, _, _ ->
            expenses.getOrNull(x.toInt())?.monthLabel ?: ""
        }
    }

    val startAxisFormatter = remember {
        CartesianValueFormatter { y, _, _ ->
            String.format(Locale.getDefault(), "€%.0f", y)
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 16.dp,
                        shape = Shape.Pill
                    )
                )
            ),
            startAxis = rememberStartAxis(
                valueFormatter = startAxisFormatter
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = bottomAxisFormatter
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}