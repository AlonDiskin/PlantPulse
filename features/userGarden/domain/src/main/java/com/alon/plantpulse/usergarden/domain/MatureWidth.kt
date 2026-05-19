package com.alon.plantpulse.usergarden.domain

/**
 * Represents the expected width or spread range of a plant when it reaches maturity.
 *
 * @property min The minimum expected width in inches.
 * @property max The maximum expected width in inches.
 */
data class MatureWidth(val min: Int, val max: Int)
