package com.alon.plantpulse.usergarden.domain

/**
 * Represents the soil temperature range required for seeds to germinate.
 *
 * @property min The minimum soil temperature in Fahrenheit.
 * @property max The maximum soil temperature in Fahrenheit.
 */
data class GerminationSoilTemp(val min: Int, val max: Int)
