package com.example.parkmate.feature_history.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkmate.feature_history.presentation.HistoryTab

@Composable
fun HistoryTopTabBar(
    selectedTab: HistoryTab,
    onTabSelected: (HistoryTab) -> Unit,
    modifier: Modifier = Modifier
){
    val backgroundColor = Color(0xFFCEC5E8)
    val activeColor = Color(0xFF7A4CAF)
    val activeTextColor = Color.White
    val inactiveTextColor = Color(0xFF32275A)

    val tabs = HistoryTab.entries

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(4.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab

                val animatedBgColor by animateColorAsState(
                    targetValue = if (isSelected) activeColor else Color.Transparent,
                    animationSpec = tween(durationMillis = 200),
                    label = "tabBgAnimation"
                )

                val label = when (tab) {
                    HistoryTab.LIST -> "List"
                    HistoryTab.ANALYTICS -> "Analytics"
                    HistoryTab.MAP -> "Heatmap"
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(animatedBgColor)
                        .clickable(
                            interactionSource = remember {MutableInteractionSource()},
                            indication = null
                        ){
                            onTabSelected(tab)
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = label,
                        color = if (isSelected) activeTextColor else inactiveTextColor,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}