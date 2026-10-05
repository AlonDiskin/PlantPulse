package com.alon.plantpulse.plantcare.domain

data class DiagnosticPlantEntity(val id: Int,
                                 val commonName: String,
                                 val lightRange: PlantLightRange) {

    fun isLightLow(lux: Float): Boolean {
        return lightRange.minLux > lux
    }

    fun isLightHigh(lux: Float): Boolean {
        return lightRange.maxLux < lux
    }

    fun isLightOk(lux: Float): Boolean {
        return lightRange.minLux <= lux && lightRange.maxLux >= lux
    }
}
