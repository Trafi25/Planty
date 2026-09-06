package com.traffipart.polanty.data.repository

import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.repository.PlantKnowledgeRepository
import javax.inject.Inject

/**
 * Implementation of [PlantKnowledgeRepository] that fetches data from the Perenual API.
 *
 * It performs a two-step process:
 * 1. Searches for the scientific name to find the exact database ID.
 * 2. Fetches full botanical details using the discovered ID.
 */
class PlantKnowledgeRepositoryImpl
    @Inject
    constructor(
        private val perenualApi: PerenualApi,
    ) : PlantKnowledgeRepository {
        /**
         * Fetches plant knowledge for a specific scientific name.
         *
         * @param scientificName The exact scientific name of the plant.
         * @return The plant knowledge or null if not found or no exact match exists.
         */
        override suspend fun getPlantKnowledge(scientificName: String): PlantKnowledge? {
            val searchResult = perenualApi.searchSpecies(scientificName)
            val exactMathc =
                searchResult.data.firstOrNull { species ->
                    species.scientificNames.any { name ->
                        name.equals(other = scientificName, ignoreCase = true)
                    }
                } ?: return null
            val details = perenualApi.getSpeciesDetails(exactMathc.id)
            return details.toDomain()
        }
    }
