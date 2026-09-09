package com.traffipart.polanty.data.repository

import android.util.Log
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.repository.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.PlantNameResolver
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
        private val plantNameResolver: PlantNameResolver,
    ) : PlantKnowledgeRepository {
        /**
         * Fetches plant knowledge for a specific scientific name.
         *
         * @param scientificName The exact scientific name of the plant.
         * @return The plant knowledge or null if not found or no exact match exists.
         */
        override suspend fun getPlantKnowledge(scientificName: String): PlantKnowledge? {
            val candidateNames = plantNameResolver.resolveNames(scientificName)
            Log.d(
                "PlantKnowledgeRepo",
                "Resolved $scientificName -> $candidateNames",
            )
            for (candidate in candidateNames) {
                Log.d(
                    "PlantKnowledgeRepo",
                    "Trying Perenual name: $candidate",
                )

                val searchResult = perenualApi.searchSpecies(candidate)
                val match =
                    searchResult.data.firstOrNull { species ->
                        species.scientificNames?.any { name ->
                            name.equals(other = candidate, ignoreCase = true)
                        } == true
                    } ?: continue
                Log.d(
                    "PlantKnowledgeRepo",
                    "Exact Perenual match: ${match.id} ${match.commonName}",
                )
                val details = perenualApi.getSpeciesDetails(match.id)
                val domainModel = details.toDomain()
                if (domainModel != null) return domainModel
            }
            Log.w(
                "PlantKnowledgeRepo",
                "No knowledge found for $scientificName",
            )
            return null
        }


    }
