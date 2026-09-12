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
