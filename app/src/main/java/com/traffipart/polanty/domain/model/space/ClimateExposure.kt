package com.traffipart.polanty.domain.model.space

/**
 * Classification of climate and weather exposure for a plant space.
 */
enum class ClimateExposure {
    /** Directly exposed to outdoor weather (e.g., Backyard, Outdoor Garden). */
    Direct,

    /** Partially sheltered from weather (e.g., Balcony, Porch). */
    Partial,

    /** Fully sheltered indoors (e.g., Living Room, Bedroom, Office). */
    Indirect,
}
