package com.example.parkmate.feature_vehiclesManager.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parkmate.R

private val ParkedBackgroundColor = Color(0xFFFFEBEE)
private val ParkedTextColor = Color(0xFFC62828)
private val AvailableBackgroundColor = Color(0xFFE8F5E9)
private val AvailableTextColor = Color(0xFF2E7D32)

@Composable
fun StatusPill(
    isParked: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isParked) ParkedBackgroundColor else AvailableBackgroundColor
    val textColor = if (isParked) ParkedTextColor else AvailableTextColor
    val text = stringResource(
        if (isParked) R.string.status_parked else R.string.status_available
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}