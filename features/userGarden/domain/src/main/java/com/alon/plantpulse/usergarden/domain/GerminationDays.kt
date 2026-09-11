package com.alon.plantpulse.usergarden.domain

/**
 * Represents the estimated range of days required for seeds to germinate (sprout).
 *
 * @property min The minimum number of days expected before germination.
 * @property max The maximum number of days expected before germination.
 */
data class GerminationDays(val min: Int, val max: Int)
