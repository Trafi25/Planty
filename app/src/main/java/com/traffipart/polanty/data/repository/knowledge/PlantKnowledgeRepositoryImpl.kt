package com.traffipart.polanty.data.repository.knowledge

import android.util.Log
import com.traffipart.polanty.core.common.trimToNull
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.mapper.toEntity
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeDao
import com.traffipart.polanty.domain.PlantKnowledgeGenerator
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.repository.knowledge.PlantKnowledgeRepository
import com.traffipart.polanty.domain.repository.knowledge.PlantNameResolver
import retrofit2.HttpException
import java.util.concurrent.CancellationException
import javax.inject.Inject

/**
 * Implementation of [PlantKnowledgeRepository] that coordinates multi-source plant knowledge retrieval.
 *
 * The repository employs a robust multi-step resolution strategy to ensure comprehensive botanical
 * details are retrieved and cached efficiently:
 * 1. **Cache Look-up:** Queries the local database (`PlantKnowledgeDao`) using a normalized, lowercase name.
 * 2. **Synonym & Name Resolution:** If a cache miss occurs, delegates to [PlantNameResolver] to obtain a prioritized list of candidate names and historical synonyms.
 * 3. **Perenual API Lookup:** Iterates over the candidate names to find an exact matching species on the Perenual API, fully supporting rate-limit handles (e.g., aborting synonym checks on a 429 error code).
 * 4. **Gemini AI Fallback:** If Perenual returns no valid match or is unavailable, falls back to [PlantKnowledgeGenerator] to use Gemini AI to generate safe, default-backed care instructions and botanical characteristics.
 *
 * All fetched data (whether from Perenual or Gemini) is immediately saved to the local database to serve subsequent requests.
 */
class PlantKnowledgeRepositoryImpl
    @Inject
    constructor(
        private val perenualApi: PerenualApi,
        private val plantNameResolver: PlantNameResolver,
        private val plantKnowledgeDao: PlantKnowledgeDao,
        private val plantKnowledgeGenerator: PlantKnowledgeGenerator,
    ) : PlantKnowledgeRepository {
        /**
         * Retrieves comprehensive botanical knowledge and care profiles for a specific plant by its scientific name.
         *
         * Executes the multi-step retrieval pipeline (Cache -> Synonyms + Perenual API -> Gemini Fallback).
         *
         * @param scientificName The raw scientific/botanical name of the target plant species.
         * @return A [PlantKnowledge] domain object containing species information and care guidance, or `null` if all retrieval strategies failed.
         */
        override suspend fun getPlantKnowledge(scientificName: String): PlantKnowledge? {
            val cleanScientificName = scientificName.trimToNull() ?: return null
            val cacheKey = cleanScientificName.lowercase()
            plantKnowledgeDao.getByScientificName(cacheKey).let {
                if (it != null) {
                    Log.d(
                        "PlantKnowledgeRepo",
                        "Cache HIT for $scientificName",
                    )
                    return it.toDomain()
                }
            }
            Log.d("PlantKnowledgeRepo", "Cache MISS for $scientificName")

            // Step 1: Resolve names and synonyms
            val candidateNames = plantNameResolver.resolveNames(cleanScientificName)
            Log.d("PlantKnowledgeRepo", "Resolved $scientificName -> $candidateNames")

            // Step 2: Try Perenual with synonyms
            val perenualResult = loadKnowledgeFromPerenual(candidateNames)
            perenualResult.knowledge?.let { knowledge ->
                plantKnowledgeDao.insert(knowledge.toEntity(cacheKey))
                return knowledge
            }

            val aiKnowledge =
                plantKnowledgeGenerator.generate(
                    scientificName = cleanScientificName,
                    commonName = perenualResult.commonName,
                ) ?: return null

            plantKnowledgeDao.insert(aiKnowledge.toEntity(cacheKey))
            return aiKnowledge
        }

        /**
         * Sequential search across Perenual endpoints using provided candidate/synonym names.
         *
         * Iterates through the given list of names and performs a species search. If a candidate name matches an
         * entry's list of official scientific names, the species details are fetched using its database ID.
         *
         * **Business Rules:**
         * - If an HTTP 429 (Too Many Requests) is encountered, the search loop is broken immediately to avoid spamming the API and respect rate limits.
         * - Saves the most recently found common name to [discoveredCommonName] to serve as fallback context for subsequent steps.
         *
         * @param candidateNames A list of potential scientific names or synonyms sorted by resolution priority.
         * @return A [Pair] containing the successfully mapped [PlantKnowledge] and the discovered common name, or `null` if no match was found or an API error occurred.
         * @throws CancellationException if the coroutine is cancelled during network operations.
         */
        private suspend fun loadKnowledgeFromPerenual(candidateNames: List<String>): PerenualLookupResult {
            var discoveredCommonName: String? = null
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

                    discoveredCommonName = match.commonName ?: discoveredCommonName
                    Log.d(
                        "PlantKnowledgeRepo",
                        "Exact Perenual match: ${match.id} ${match.commonName}",
                    )

                    val details = perenualApi.getSpeciesDetails(match.id)
                    Log.d("PlantKnowledgeRepo", "Perenual details parsed successfully for ID=${match.id}")

                    val domainModel = details.toDomain()
                    if (domainModel != null) return PerenualLookupResult(domainModel, discoveredCommonName)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: HttpException) {
                    Log.w("PlantKnowledgeRepo", "Perenual HTTP ${e.code()} for $candidate")
                    if (e.code() == 429) break // Stop trying synonyms if rate limited
                } catch (e: Exception) {
                    Log.w("PlantKnowledgeRepo", "Perenual failed for $candidate", e)
                }
            }
            return PerenualLookupResult(null, discoveredCommonName)
        }
    }

private data class PerenualLookupResult(
    val knowledge: PlantKnowledge?,
    val commonName: String?,
)
