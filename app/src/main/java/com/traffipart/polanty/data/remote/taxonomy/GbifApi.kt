package com.traffipart.polanty.data.remote.taxonomy

import com.traffipart.polanty.data.remote.taxonomy.dto.GbifMatchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GbifApi {

    @GET("v2/species/match")
    suspend fun matchSpecies(
        @Query("scientificName") scientificName: String,
        @Query("kingdom") kingdom: String = "Plantae"
    ) : GbifMatchResponseDto


}