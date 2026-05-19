package com.alon.plantpulse.usergarden.domain

/**
 * Represents the range of days from planting until the plant reaches maturity.
 *
 * @property min The minimum number of days expected to reach maturity.
 * @property max The maximum number of days expected to reach maturity.
 */
data class MaturityDays(val min: Int, val max: Int)
