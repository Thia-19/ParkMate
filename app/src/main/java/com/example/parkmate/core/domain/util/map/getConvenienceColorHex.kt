package com.example.parkmate.core.domain.util.map

private const val SCORE_EXCELLENT = 75.0
private const val SCORE_GOOD = 50.0
private const val SCORE_FAIR = 30.0
private const val SCORE_POOR = 15.0

fun getConvenienceColorHex(score: Double): String = when {
    score >= SCORE_EXCELLENT -> "#4CAF50"
    score >= SCORE_GOOD -> "#8BC34A"
    score >= SCORE_FAIR -> "#FFEB3B"
    score >= SCORE_POOR -> "#FF9800"
    else -> "#F44336"
}