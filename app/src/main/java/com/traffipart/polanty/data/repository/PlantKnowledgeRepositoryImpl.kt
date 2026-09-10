package com.traffipart.polanty.data.repository

import android.util.Log
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.mapper.toEntity
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeDao
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
        private val plantKnowledgeDao: PlantKnowledgeDao
    ) : PlantKnowledgeRepository {


        override suspend fun getPlantKnowledge(scientificName: String): PlantKnowledge? {
            val normalizedName = scientificName.trim().lowercase()
            if (normalizedName.isEmpty()) return null
            val cached = plantKnowledgeDao.getByScientificName(normalizedName)
            if (cached != null) {
                Log.d(
                    "PlantKnowledgeRepo",
                    "Cache HIT for $scientificName",
                )
                return cached.toDomain()
            }
            Log.d(
                "PlantKnowledgeRepo",
                "Cache MISS for $scientificName",
            )
            val remoteKnowledge = loadKnowledgeFromRemote(scientificName.trim()) ?: return null

            plantKnowledgeDao.insert(remoteKnowledge.toEntity(normalizedName))
            return remoteKnowledge
        }

        /**
         * Fetches plant knowledge for a specific scientific name.
         *
         * @param scientificName The exact scientific name of the plant.
         * @return The plant knowledge or null if not found or no exact match exists.
         */
        private suspend fun loadKnowledgeFromRemote(scientificName: String): PlantKnowledge? {
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
                val details =
                    try {
                        perenualApi.getSpeciesDetails(
                            match.id,
                        )
                    } catch (e: Exception) {
                        Log.e(
                            "PlantKnowledgeRepo",
                            """
                                Failed to load/parse Perenual details.
                                id=${match.id}
                                candidate=$candidate
                                exception=${e::class.simpleName}
                                message=${e.message}
                                """.trimIndent(),
                            e,
                        )

                        throw e
                    }

                Log.d(
                    "PlantKnowledgeRepo",
                    "Perenual details parsed successfully for ID=${match.id}",
                )
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
