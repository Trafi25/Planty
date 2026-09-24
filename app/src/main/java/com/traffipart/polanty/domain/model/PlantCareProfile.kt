package com.traffipart.polanty.domain.model

/**
 * Represents comprehensive care instructions for a plant species.
 *
 * @property scientificName The scientific botanical name of the plant.
 * @property watering Specific watering and soil check profile.
 * @property light Preferred lighting conditions.
 * @property humidity Target relative humidity percentage range.
 * @property temperature Optimal ambient temperature range in Celsius.
 * @property fertilizing Summary of fertilization guidelines.
 */
data class PlantCareProfile(
    val scientificName: String,
    val watering: WateringProfile?,
    val light: LightRequirement?,
    val humidity: HumidityRange?,
    val temperature: TemperatureRange?,
    val fertilizing: String?,
)

/**
 * Watering guidelines including recommended check interval ranges in days.
 *
 * @property soilCheckIntervalDaysMin Minimum recommended days between soil moisture checks.
 * @property soilCheckIntervalDaysMax Maximum recommended days between soil moisture checks.
 * @property instruction Contextual watering advice.
 */
data class WateringProfile(
    val soilCheckIntervalDaysMin: Int,
    val soilCheckIntervalDaysMax: Int,
    val instruction: String,
)

/**
 * Relative humidity range percentage.
 *
 * @property minPercent Minimum recommended relative humidity percentage.
 * @property maxPercent Maximum recommended relative humidity percentage.
 */
data class HumidityRange(
    val minPercent: Int,
    val maxPercent: Int,
)

/**
 * Ambient temperature range in degrees Celsius.
 *
 * @property minCelsius Minimum recommended temperature in Celsius.
 * @property maxCelsius Maximum recommended temperature in Celsius.
 */
data class TemperatureRange(
    val minCelsius: Double,
    val maxCelsius: Double,
)

/**
 * Categorization of sunlight/light requirements.
 */
enum class LightRequirement {
    Low,
    MediumIndirect,
    BrightIndirect,
    Direct,
}
