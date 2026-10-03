package com.traffipart.polanty.data.remote.weather.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMeteoResponseDto(
    val current: CurrentWeatherDto?,
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "temperature_2m")
    val temperatureCelsius: Double?,
    @Json(name = "relative_humidity_2m")
    val humidityPercent: Double?,
)
