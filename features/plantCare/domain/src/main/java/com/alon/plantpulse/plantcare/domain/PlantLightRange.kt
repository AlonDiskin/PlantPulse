package com.alon.plantpulse.plantcare.domain

enum class PlantLightRange(val minLux: Float, val maxLux: Float) {
    FULL(5000f,10000f),
    PARTIAL(1500f,5000f),
    SHADE(500f,1500f)
}