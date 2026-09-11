package com.alon.plantpulse.usergarden.domain

/**
 * Represents the recommended distance range between plants in a row.
 *
 * @property min The minimum recommended spacing in inches.
 * @property max The maximum recommended spacing in inches.
 */
data class RowSpacing(val min: Int, val max: Int)
