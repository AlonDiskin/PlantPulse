package com.alon.plantpulse.usergarden.domain

/**
 * Represents the expected height range of a plant when it reaches maturity.
 *
 * @property min The minimum expected height in inches.
 * @property max The maximum expected height in inches.
 */
data class MatureHeight(val min: Int, val max: Int)
