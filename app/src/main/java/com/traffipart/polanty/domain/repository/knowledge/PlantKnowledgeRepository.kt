package com.traffipart.polanty.domain.repository.knowledge

import com.traffipart.polanty.domain.model.PlantKnowledge

/**
 * Repository interface for retrieving detailed botanical knowledge about plant species.
 */
interface PlantKnowledgeRepository {
    /**
     * Fetches detailed information (care, toxicity, origin) for a specific scientific name.
     *
     * @param scientificName The exact scientific name of the plant.
     * @return The [PlantKnowledge] domain model if found, null otherwise.
     */
    suspend fun getPlantKnowledge(scientificName: String): PlantKnowledge?
}
