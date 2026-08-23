package com.example.parkmate.core.domain.utils.map


fun getConvenienceColorHex(score: Double): String {
    return when {
        score >= 75.0 -> "#4CAF50" // Verde         : Comodo
        score >= 50.0 -> "#8BC34A" // Verde Chiaro
        score >= 30.0 -> "#FFEB3B" // Giallo
        score >= 15.0 -> "#FF9800" // Arancione
        else -> "#F44336"          // Rosso         : Molto scomodo
    }
}