package com.example.parkmate.feature_parkingEffort.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

data class PulsingWaveState(
    val scale: State<Float>,
    val alpha: State<Float>
)

@Composable
fun rememberPulsingCircleAnimation(waveCount: Int = 3): List<PulsingWaveState> {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsingCircleTransition")

    return List(waveCount) { index ->
        val delayOffset = index * 1000

        val scale = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 6000
                    0.0f at 0 using LinearEasing
                    0.2f at (1000 + delayOffset) using LinearEasing
                    1.0f at (3000 + delayOffset) using LinearEasing
                    1.2f at (4500 + delayOffset) using LinearEasing
                    1.4f at 6000 using LinearEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "scale_$index"
        )

        val alpha = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 6000
                    0.0f at 0 using LinearEasing
                    0.7f at (1000 + delayOffset) using LinearEasing
                    0.35f at (3500 + delayOffset) using LinearEasing
                    0.0f at 6000 using LinearEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "alpha_$index"
        )

        PulsingWaveState(scale = scale, alpha = alpha)
    }
}