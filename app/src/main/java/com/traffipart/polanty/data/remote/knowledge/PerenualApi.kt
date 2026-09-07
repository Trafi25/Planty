package com.traffipart.polanty.data.remote.knowledge

import com.traffipart.polanty.data.remote.knowledge.dtos.PerenualSpeciesDetailsDto
import com.traffipart.polanty.data.remote.knowledge.dtos.PerenualSpeciesListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the Perenual Plant API.
 * Provides access to species searching and detailed botanical information.
 */
interface PerenualApi {
    /**
     * Searches for plant species matching a specific query string.
     *
     * @param query The search query (e.g., scientific or common name).
     * @return A list of species summaries matching the query.
     */
    @GET("api/species-list")
    suspend fun searchSpecies(
        @Query("q") query: String,
    ): PerenualSpeciesListResponseDto

    /**
     * Retrieves full botanical and care details for a specific plant species.
     *
     * @param id The unique identifier for the species in the Perenual database.
     * @return The detailed botanical information for the requested species.
     */
    @GET("api/species/details/{id}")
    suspend fun getSpeciesDetails(
        @Path("id") id: Int,
    ): PerenualSpeciesDetailsDto
}
