package com.example.parkmate.core.domain.util.map

fun getConvenienceColorHex(score: Double): String = when {
    score >= 75.0 -> "#4CAF50" // Verde: Ottimo (Molto vicino)
    score >= 50.0 -> "#8BC34A" // Verde chiaro: Buono
    score >= 30.0 -> "#FFEB3B" // Giallo: Accettabile / Medio
    score >= 15.0 -> "#FF9800" // Arancione: Scomodo
    else -> "#F44336"          // Rosso: Pessimo (Molto lontano)
}