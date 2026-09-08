package com.traffipart.polanty.data.repository

import android.util.Log
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
            Log.d("PlantKnowledgeRepo", "Searching for knowledge: $scientificName")
            val searchResult = perenualApi.searchSpecies(scientificName)
            Log.d("PlantKnowledgeRepo", "Found ${searchResult.data.size} potential matches")

            val match =
                searchResult.data.firstOrNull { species ->
                    species.scientificNames?.any { name ->
                        name.equals(other = scientificName, ignoreCase = true)
                    } == true
                } ?: searchResult.data.firstOrNull()

            if (match == null) {
                Log.w("PlantKnowledgeRepo", "No matching species found for $scientificName")
                return null
            }

            Log.d("PlantKnowledgeRepo", "Fetching details for ID: ${match.id} (${match.commonName})")
            val details = perenualApi.getSpeciesDetails(match.id)
            Log.d(
                "PlantKnowledgeRepo",
                """
    DETAILS:
    scientificNames=${details.scientificNames}
    watering=${details.watering}
    wateringBenchmark=${details.wateringBenchmark}
    sunlight=${details.sunlight}
    origin=${details.origin}
    dimensions=${details.dimensions}
    description=${details.description}
    poisonousToPets=${details.poisonousToPets}
    poisonousToHumans=${details.poisonousToHumans}
    """.trimIndent(),
            )
            val domainModel = details.toDomain()

            if (domainModel == null) {
                Log.e("PlantKnowledgeRepo", "Failed to map details to domain model for ID: ${match.id}")
            } else {
                Log.d("PlantKnowledgeRepo", "Successfully retrieved knowledge for $scientificName")
            }

            return domainModel
        }
    }
