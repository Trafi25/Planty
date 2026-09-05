package com.traffipart.polanty.domain.model

data class PlantCareProfile(
    val scientificName: String,
    val watering: WateringProfile,
    val light: LightRequirement,
    val humidity: HumidityRange?,
    val temperature: TemperatureRange?,
    val fertilizing: String?,
)

data class WateringProfile(
    val soilCheckIntervalDaysMin: Int,
    val soilCheckIntervalDaysMax: Int,
    val instruction: String,
)

data class HumidityRange(
    val minPercent: Int,
    val maxPercent: Int,
)

data class TemperatureRange(
    val minCelsius: Double,
    val maxCelsius: Double,
)

enum class LightRequirement {
    Low,
    MediumIndirect,
    BrightIndirect,
    Direct,
}
