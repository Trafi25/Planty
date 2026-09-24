package com.traffipart.polanty.domain.model

/**
 * Aggregate domain model containing species information and care requirements for a plant.
 *
 * @property speciesInfo General species details including description, origin, and toxicity.
 * @property careProfile Optimal care parameters including watering, lighting, and humidity guidelines.
 */
data class PlantKnowledge(
    val speciesInfo: PlantSpeciesInfo,
    val careProfile: PlantCareProfile?,
)
