package com.traffipart.polanty.domain.repository.knowledge

/**
 * Interface for resolving botanical scientific names and discovering potential synonyms.
 */
interface PlantNameResolver {
    /**
     * Resolves the given scientific name into a list of valid botanical names and synonyms.
     *
     * @param scientificName The raw scientific name to resolve.
     * @return A list of alternative names or synonyms, sorted by relevance.
     */
    suspend fun resolveNames(scientificName: String): List<String>
}
