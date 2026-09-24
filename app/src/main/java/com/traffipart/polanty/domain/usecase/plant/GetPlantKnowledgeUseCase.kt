package com.traffipart.polanty.domain.usecase.plant

import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import javax.inject.Inject

/**
 * Use case to retrieve botanical knowledge for a plant species by its scientific name.
 *
 * @property repository The repository providing plant knowledge retrieval and caching.
 */
class GetPlantKnowledgeUseCase
    @Inject
    constructor(
        private val repository: PlantKnowledgeRepository,
    ) {
        /**
         * Fetches botanical knowledge for the given [scientificName].
         *
         * @param scientificName The scientific name of the target plant species.
         * @return The [PlantKnowledge] domain model, or `null` if not found or if the name is blank.
         */
        suspend operator fun invoke(scientificName: String): PlantKnowledge? {
            val normalizedName = scientificName.trim()
            if (normalizedName.isEmpty()) return null
            return repository.getPlantKnowledge(normalizedName)
        }
    }
