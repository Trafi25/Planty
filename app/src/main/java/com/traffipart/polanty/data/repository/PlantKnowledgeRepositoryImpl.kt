package com.traffipart.polanty.data.repository

import android.util.Log
import com.traffipart.polanty.core.common.trimToNull
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.mapper.toEntity
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeDao
import com.traffipart.polanty.domain.PlantKnowledgeGenerator
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.repository.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.PlantNameResolver
import retrofit2.HttpException
import java.util.concurrent.CancellationException
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
        private val plantKnowledgeDao: PlantKnowledgeDao,
        private val plantKnowledgeGenerator: PlantKnowledgeGenerator,
    ) : PlantKnowledgeRepository {
        override suspend fun getPlantKnowledge(scientificName: String): PlantKnowledge? {
            val normalizedName = scientificName.trimToNull()?.lowercase() ?: return null
            plantKnowledgeDao.getByScientificName(normalizedName).let {
                if (it != null) {
                    Log.d(
                        "PlantKnowledgeRepo",
                        "Cache HIT for $scientificName",
                    )
                    return it.toDomain()
                }
            }
            Log.d(
                "PlantKnowledgeRepo",
                "Cache MISS for $scientificName",
            )

            // Step 1: Resolve names and synonyms
            val candidateNames = plantNameResolver.resolveNames(normalizedName)
            Log.d("PlantKnowledgeRepo", "Resolved $scientificName -> $candidateNames")

            // Step 2: Try Perenual with synonyms
            val perenualResult = loadKnowledgeFromPerenual(candidateNames)
            if (perenualResult != null) {
                plantKnowledgeDao.insert(perenualResult.first.toEntity(normalizedName))
                return perenualResult.first
            }

            // Step 3: Gemini Fallback
            // Use the common name found during Perenual search if possible
            val discoveredCommonName = lastDiscoveredCommonName
            Log.d("PlantKnowledgeRepo", "Using Gemini fallback for $normalizedName (Common Name: $discoveredCommonName)")
            
            val aiKnowledge = plantKnowledgeGenerator.generate(
                scientificName = normalizedName,
                commonName = discoveredCommonName
            ) ?: return null

            plantKnowledgeDao.insert(aiKnowledge.toEntity(normalizedName))
            return aiKnowledge
        }

        private var lastDiscoveredCommonName: String? = null

        /**
         * Fetches plant knowledge from Perenual by trying various candidate names.
         * Returns a Pair of the knowledge and the common name discovered during search.
         */
        private suspend fun loadKnowledgeFromPerenual(candidateNames: List<String>): Pair<PlantKnowledge, String?>? {
            lastDiscoveredCommonName = null
            for (candidate in candidateNames) {
                try {
                    Log.d("PlantKnowledgeRepo", "Trying Perenual name: $candidate")
                    val searchResult = perenualApi.searchSpecies(candidate)
                    val match =
                        searchResult.data.firstOrNull { species ->
                            species.scientificNames?.any { name ->
                                name.equals(other = candidate, ignoreCase = true)
                            } == true
                        } ?: continue

                    lastDiscoveredCommonName = match.commonName
                    Log.d(
                        "PlantKnowledgeRepo",
                        "Exact Perenual match: ${match.id} ${match.commonName}",
                    )

                    val details = perenualApi.getSpeciesDetails(match.id)
                    Log.d("PlantKnowledgeRepo", "Perenual details parsed successfully for ID=${match.id}")
                    
                    val domainModel = details.toDomain()
                    if (domainModel != null) return domainModel to match.commonName
                } catch (e: CancellationException) {
                    throw e
                } catch (e: HttpException) {
                    Log.w("PlantKnowledgeRepo", "Perenual HTTP ${e.code()} for $candidate")
                    if (e.code() == 429) break // Stop trying synonyms if rate limited
                } catch (e: Exception) {
                    Log.w("PlantKnowledgeRepo", "Perenual failed for $candidate", e)
                }
            }
            return null
        }
    }
