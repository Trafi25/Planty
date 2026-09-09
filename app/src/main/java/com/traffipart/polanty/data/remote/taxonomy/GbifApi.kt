package com.traffipart.polanty.data.remote.taxonomy

import com.traffipart.polanty.data.remote.taxonomy.dto.GbifMatchResponseDto
import com.traffipart.polanty.data.remote.taxonomy.dto.GbifSynonymsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GbifApi {

    @GET("v2/species/match")
    suspend fun matchSpecies(
        @Query("scientificName") scientificName: String,
        @Query("kingdom") kingdom: String = "Plantae"
    ) : GbifMatchResponseDto

    @GET("v1/species/{usageKey}/synonyms")
    suspend fun getSynonyms(
    @Path("userKey")
    usagKey: String,
    @Query("limit")
    limit: Int = 100,
    ): GbifSynonymsResponseDto
}
