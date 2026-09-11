package com.alon.plantpulse.usergarden.domain

/**
 * Represents the general classification of a plant.
 */
enum class PlantCategory {
    /**
     * Edible plants grown for their nutritional value, excluding sweet fruits and herbs.
     */
    VEGETABLE,

    /**
     * Plants used for flavoring, medicine, or perfume.
     */
    HERB,

    /**
     * Seed-bearing structures of flowering plants.
     */
    FRUIT,

    /**
     * Small, pulpy, and often edible fruits.
     */
    BERRY,

    /**
     * Plants grown primarily for their decorative blooms.
     */
    FLOWER,

    /**
     * Plants grown primarily to manage soil erosion, soil fertility, soil quality, water, weeds, pests, diseases, biodiversity and wildlife in an agroecosystem.
     */
    COVER_CROP
}
