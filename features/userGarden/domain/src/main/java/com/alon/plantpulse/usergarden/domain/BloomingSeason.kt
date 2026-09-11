package com.alon.plantpulse.usergarden.domain

/**
 * Represents the season or climate conditions during which a plant typically flowers or thrives.
 */
enum class BloomingSeason {
    /**
     * Plants that live for more than two years and usually bloom every year.
     */
    PERENNIAL,

    /**
     * Plants that thrive and bloom in cooler temperatures, typically in spring or fall.
     */
    COOL,

    /**
     * Plants that thrive and bloom in warmer temperatures, typically in the height of summer.
     */
    WARM
}
