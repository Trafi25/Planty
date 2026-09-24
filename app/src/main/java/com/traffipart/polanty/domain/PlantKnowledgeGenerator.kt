package com.traffipart.polanty.domain

import com.traffipart.polanty.domain.model.PlantKnowledge

/**
 * Interface for AI-backed or automated generation of plant knowledge and care profiles.
 */
interface PlantKnowledgeGenerator {
    /**
     * Generates a detailed [PlantKnowledge] profile for the given plant species.
     *
     * @param scientificName Scientific botanical name of the plant.
     * @param commonName Optional primary common name of the plant.
     * @return The generated [PlantKnowledge] domain model, or `null` if generation fails.
     */
    suspend fun generate(
        scientificName: String,
        commonName: String?,
    ): PlantKnowledge?
}
