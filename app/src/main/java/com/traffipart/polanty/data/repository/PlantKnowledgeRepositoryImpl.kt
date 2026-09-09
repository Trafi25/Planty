package com.traffipart.polanty.data.repository

import android.util.Log
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.repository.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.PlantNameResolver
import com.traffipart.polanty.domain.storage.PlantImageStorage
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
            Log.d("PlantKnowledgeRepo", "Searching for knowledge: $scientificName")
            for (candidate in candidateNames) {
                val searchResult = perenualApi.searchSpecies(candidate)
                Log.d("PlantKnowledgeRepo", "Found ${searchResult.data.size} potential matches")
                val match =
                    searchResult.data.firstOrNull { species ->
                        species.scientificNames?.any { name ->
                            name.equals(other = candidate, ignoreCase = true)
                        } == true
                    } ?: continue
                if (match == null) {
                    Log.w("PlantKnowledgeRepo", "No matching species found for $scientificName")
                    return null
                }
                Log.d("PlantKnowledgeRepo", "Fetching details for ID: ${match.id} (${match.commonName})")
                val details = perenualApi.getSpeciesDetails(match.id)
                val domainModel = details.toDomain()
                if (domainModel != null) return domainModel
            }          
            return null
        }


    }
