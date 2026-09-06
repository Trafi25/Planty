package com.traffipart.polanty.data.remote.knowledge

import com.traffipart.polanty.data.remote.knowledge.dtos.PerenualSpeciesDetailsDto
import com.traffipart.polanty.data.remote.knowledge.dtos.PerenualSpeciesListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PerenualApi {
    @GET("api/v2/species-list")
    suspend fun searchSpecies(
        @Query("q") query: String,
    ): PerenualSpeciesListResponseDto

    @GET("api/v2/species/details/{id}")
    suspend fun getSpeciesDetails(
        @Path("id") id: Int,
    ): PerenualSpeciesDetailsDto
}
