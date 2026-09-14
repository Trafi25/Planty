package com.traffipart.polanty.data.mapper

import com.traffipart.polanty.core.common.trimToNull
import com.traffipart.polanty.data.remote.knowledge.dtos.GeminiPlantKnowledgeDto
import com.traffipart.polanty.domain.model.HumidityRange
import com.traffipart.polanty.domain.model.LightRequirement
import com.traffipart.polanty.domain.model.PlantCareProfile
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.model.PlantSpeciesInfo
import com.traffipart.polanty.domain.model.PlantToxicity
import com.traffipart.polanty.domain.model.TemperatureRange
import com.traffipart.polanty.domain.model.ToxicityLevel
import com.traffipart.polanty.domain.model.WateringProfile

/**
 * Maps the [GeminiPlantKnowledgeDto] received from the AI model to the domain [PlantKnowledge] model.
 *
 * This mapper includes strict validation for ranges (humidity, temperature, watering) to handle
 * potential inconsistencies in AI-generated data.
 *
 * @param requestedScientificName The scientific name used in the original request.
 * @param fallbackCommonName An optional common name to use if the DTO doesn't provide one.
 */
fun GeminiPlantKnowledgeDto.toDomain(
    requestedScientificName: String,
    fallbackCommonName: String?,
): PlantKnowledge? {
    val cleanScientificName = requestedScientificName.trimToNull() ?: return null
    val cleanDescription = description.trimToNull() ?: return null

    val watering =
        createWateringProfile(
            minDays = wateringDaysMin,
            maxDays = wateringDaysMax,
            instruction = wateringInstruction,
        )

    val humidity =
        createHumidityRange(
            minPercent = humidityMinPercent,
            maxPercent = humidityMaxPercent,
        )

    val temperature =
        createTemperatureRange(
            minCelsius = temperatureMinCelsius,
            maxCelsius = temperatureMaxCelsius,
        )

    val light = lightRequirement.toLightRequirement()
    val cleanFertilizing = fertilizing.trimToNull()

    val careProfile =
        if (
            watering != null ||
            light != null ||
            humidity != null ||
            temperature != null ||
            cleanFertilizing != null
        ) {
            PlantCareProfile(
                scientificName = cleanScientificName,
                watering = watering,
                light = light,
                humidity = humidity,
                temperature = temperature,
                fertilizing = cleanFertilizing,
            )
        } else {
            null
        }

    val heightRange =
        createHeightRange(
            minCm = typicalHeightCmMin,
            maxCm = typicalHeightCmMax,
        )

    return PlantKnowledge(
        speciesInfo =
            PlantSpeciesInfo(
                scientificName = cleanScientificName,
                commonName = commonName.trimToNull() ?: fallbackCommonName.trimToNull(),
                description = cleanDescription,
                origin = origin.trimToNull(),
                toxicity =
                    PlantToxicity(
                        pets = petToxicity.toToxicityLevel(),
                        humans = humanToxicity.toToxicityLevel(),
                        notes = toxicityNotes.trimToNull(),
                    ),
                typicalHeightCmMin = heightRange?.first,
                typicalHeightCmMax = heightRange?.second,
            ),
        careProfile = careProfile,
    )
}

/**
 * Validates and constructs a [WateringProfile] from the AI-generated numbers.
 *
 * **Validation Rules:**
 * - Minimum and maximum check days must fall within a logical annual range (1 to 365 days).
 * - The instruction string must not be empty or blank.
 * - The minimum check days must be less than or equal to the maximum check days.
 *
 * @param minDays The AI-provided minimum days between soil checks.
 * @param maxDays The AI-provided maximum days between soil checks.
 * @param instruction Narrative guidelines on watering indications.
 * @return A valid [WateringProfile], or `null` if any validation rule is violated.
 */
private fun createWateringProfile(
    minDays: Int?,
    maxDays: Int?,
    instruction: String?,
): WateringProfile? {
    val validMin = minDays?.takeIf { it in 1..365 }
    val validMax = maxDays?.takeIf { it in 1..365 }
    val validInstruction = instruction.trimToNull()

    if (
        validMin == null ||
        validMax == null ||
        validInstruction == null ||
        validMin > validMax
    ) {
        return null
    }

    return WateringProfile(
        soilCheckIntervalDaysMin = validMin,
        soilCheckIntervalDaysMax = validMax,
        instruction = validInstruction,
    )
}

/**
 * Validates and constructs a [HumidityRange] from AI-generated parameters.
 *
 * **Validation Rules:**
 * - Percentages must be clamped between 0% and 100%.
 * - Minimum humidity must be less than or equal to maximum humidity.
 *
 * @param minPercent The AI-provided minimum humidity percentage.
 * @param maxPercent The AI-provided maximum humidity percentage.
 * @return A valid [HumidityRange], or `null` if the parameters are inconsistent or out of bounds.
 */
private fun createHumidityRange(
    minPercent: Int?,
    maxPercent: Int?,
): HumidityRange? {
    val validMin = minPercent?.takeIf { it in 0..100 }
    val validMax = maxPercent?.takeIf { it in 0..100 }

    if (
        validMin == null ||
        validMax == null ||
        validMin > validMax
    ) {
        return null
    }

    return HumidityRange(
        minPercent = validMin,
        maxPercent = validMax,
    )
}

/**
 * Validates and constructs a [TemperatureRange] from AI-generated parameters.
 *
 * **Validation Rules:**
 * - Temperatures must be finite double numbers.
 * - Values must reside within reasonable global botanical extremes (-50.0°C to 80.0°C).
 * - Minimum temperature must be less than or equal to maximum temperature.
 *
 * @param minCelsius The AI-provided minimum ideal temperature.
 * @param maxCelsius The AI-provided maximum ideal temperature.
 * @return A valid [TemperatureRange], or `null` if values are infinite or out of bounds.
 */
private fun createTemperatureRange(
    minCelsius: Double?,
    maxCelsius: Double?,
): TemperatureRange? {
    val validMin = minCelsius?.takeIf { it.isFinite() && it in -50.0..80.0 }
    val validMax = maxCelsius?.takeIf { it.isFinite() && it in -50.0..80.0 }

    if (
        validMin == null ||
        validMax == null ||
        validMin > validMax
    ) {
        return null
    }

    return TemperatureRange(
        minCelsius = validMin,
        maxCelsius = validMax,
    )
}

/**
 * Validates and constructs an ideal height range from AI-generated parameters.
 *
 * **Validation Rules:**
 * - Both bounds must be positive, non-zero values (> 0 cm).
 * - Minimum typical height must be less than or equal to maximum typical height.
 *
 * @param minCm The AI-provided minimum typical height in centimeters.
 * @param maxCm The AI-provided maximum typical height in centimeters.
 * @return A [Pair] containing verified minimum and maximum heights, or `null` if validation fails.
 */
private fun createHeightRange(
    minCm: Int?,
    maxCm: Int?,
): Pair<Int, Int>? {
    val validMin = minCm?.takeIf { it > 0 }
    val validMax = maxCm?.takeIf { it > 0 }

    if (
        validMin == null ||
        validMax == null ||
        validMin > validMax
    ) {
        return null
    }

    return validMin to validMax
}

/**
 * Normalizes a raw string representation of a toxicity level into a robust [ToxicityLevel] enum.
 * Removes alphanumeric formatting anomalies and strips whitespace before mapping.
 *
 * @return The matched [ToxicityLevel], or [ToxicityLevel.Unknown] if the string does not match any entry.
 */
private fun String?.toToxicityLevel(): ToxicityLevel {
    val normalizedValue =
        this
            ?.filter { it.isLetterOrDigit() }
            ?.lowercase()
            ?: return ToxicityLevel.Unknown

    return ToxicityLevel.entries.firstOrNull { level ->
        level.name
            .filter { it.isLetterOrDigit() }
            .lowercase() == normalizedValue
    } ?: ToxicityLevel.Unknown
}

/**
 * Normalizes a raw string representation of light requirements into a matching [LightRequirement] enum.
 * Removes non-alphanumeric characters and ignores case during comparison.
 *
 * @return The matched [LightRequirement], or `null` if the string doesn't correspond to a known requirement.
 */
private fun String?.toLightRequirement(): LightRequirement? {
    val normalizedValue =
        this
            ?.filter { it.isLetterOrDigit() }
            ?.lowercase()
            ?: return null

    return LightRequirement.entries.firstOrNull { requirement ->
        requirement.name
            .filter { it.isLetterOrDigit() }
            .lowercase() == normalizedValue
    }
}
