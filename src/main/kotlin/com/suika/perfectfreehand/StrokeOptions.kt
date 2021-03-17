package com.suika.perfectfreehand

data class StrokeOptions(
    val size: Double = 8.0,
    val thinning: Double = 0.5,
    val smoothing: Double = 0.5,
    val streamline: Double = 0.5,
    val easing: ((Double) -> Double) = { it },
    val simulatePressure: Boolean = true,
)
