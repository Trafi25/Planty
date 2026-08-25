package com.traffipart.polanty.data.remote.plant

import com.traffipart.polanty.data.remote.plant.dto.PlantNetResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PlantNetApi {
    @Multipart
    @POST("v2/identify/all")
    suspend fun identifyPlant(
        @Query("api-key") apiKey: String,
        @Query("lang") language: String = "en",
        @Query("nb-results") numberOfResults: Int = 5,
        @Part image: MultipartBody.Part,
    ): PlantNetResponseDto
}
