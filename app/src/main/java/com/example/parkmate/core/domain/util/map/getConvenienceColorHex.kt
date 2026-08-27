package com.example.parkmate.core.domain.util.map

fun getConvenienceColorHex(score: Double): String = when {
    score >= 75.0 -> "#4CAF50"
    score >= 50.0 -> "#8BC34A"
    score >= 30.0 -> "#FFEB3B"
    score >= 15.0 -> "#FF9800"
    else -> "#F44336"
}