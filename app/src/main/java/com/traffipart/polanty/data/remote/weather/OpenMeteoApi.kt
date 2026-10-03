package com.traffipart.polanty.data.remote.weather

import com.traffipart.polanty.data.remote.weather.dto.OpenMeteoResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_VARIABLES,
    ): OpenMeteoResponseDto

    private companion object {
        const val CURRENT_VARIABLES =
            "temperature_2m,relative_humidity_2m"
    }
}
