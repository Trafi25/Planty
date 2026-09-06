package com.traffipart.polanty.data.mapper

import com.traffipart.polanty.data.remote.knowledge.dtos.PerenualSpeciesDetailsDto
import com.traffipart.polanty.domain.model.LightRequirement
import com.traffipart.polanty.domain.model.PlantCareProfile
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.model.PlantSpeciesInfo
import com.traffipart.polanty.domain.model.PlantToxicity
import com.traffipart.polanty.domain.model.ToxicityLevel
import com.traffipart.polanty.domain.model.WateringProfile

/**
 * Maps the [PerenualSpeciesDetailsDto] to the domain [PlantKnowledge] model.
 *
 * This mapper handles unit conversions for dimensions, string normalization for
 * light requirements, and regex parsing for watering intervals.
 *
 * @return The mapped [PlantKnowledge] or null if scientific name is missing.
 */
fun PerenualSpeciesDetailsDto.toDomain(): PlantKnowledge? {
    val scientificName = scientificNames.firstOrNull() ?: return null
    val wateringRange = wateringBenchmark?.value.toDayRange()
    val lightRequirement = sunlight?.toLightRequirement()

    val careProfile =
        if (
            wateringRange != null && lightRequirement != null
        ) {
            PlantCareProfile(
                scientificName = scientificName,
                watering =
                    WateringProfile(
                        soilCheckIntervalDaysMin = wateringRange.first,
                        soilCheckIntervalDaysMax = wateringRange.second,
                        instruction = watering ?: "Check the soil before watering.",
                    ),
                light = lightRequirement,
                humidity = null,
                temperature = null,
                fertilizing = null,
            )
        } else {
            null
        }
    return PlantKnowledge(
        speciesInfo =
            PlantSpeciesInfo(
                scientificName = scientificName,
                commonName = commonName,
                description = description.orEmpty(),
                origin = origin?.joinToString(", ") ?: "Unknown",
                toxicity =
                    PlantToxicity(
                        pets = poisonousToPets.toToxicity(),
                        humans = poisonousToHumans.toToxicity(),
                        notes = null,
                    ),
                typicalHeightCmMin = dimensions?.minValue?.toCentimeters(dimensions.unit),
                typicalHeightCmMax = dimensions?.maxValue?.toCentimeters(dimensions.unit),
            ),
        careProfile = careProfile,
    )
}

private fun Double.toCentimeters(unit: String?) =
    when (
        unit
            ?.trim()
            ?.lowercase()
    ) {
        "cm",
        "centimeter",
        "centimeters",
        -> toInt()

        "m",
        "meter",
        "meters",
        -> (this * 100).toInt()

        "ft",
        "feet",
        "foot",
        -> (this * 30.48).toInt()

        "in",
        "inch",
        "inches",
        -> (this * 2.54).toInt()

        else -> null
    }

private fun Boolean?.toToxicity(): ToxicityLevel =
    when (this) {
        true -> ToxicityLevel.Toxic
        false -> ToxicityLevel.NonToxic
        null -> ToxicityLevel.Unknown
    }

private fun List<String>?.toLightRequirement(): LightRequirement? {
    val values = this?.joinToString(" ")?.lowercase() ?: return null

    return when {
        "full sun" in values ->
            LightRequirement.Direct
        "part shade" in values ->
            LightRequirement.MediumIndirect
        "full shade" in values ->
            LightRequirement.Low
        "indirect" in values ->
            LightRequirement.BrightIndirect
        else -> null
    }
}

private fun Any?.toDayRange(): Pair<Int, Int>? {
    val text =
        when (this) {
            is Number -> toInt().toString()
            is String -> this
            else -> return null
        }
    val numbers =
        Regex("\\d+")
            .findAll(text)
            .mapNotNull {
                it.value.toIntOrNull()
            }.toList()
    return when {
        numbers.size >= 2 -> numbers[0] to numbers[1]
        numbers.size == 1 -> numbers[0] to numbers[0]
        else -> null
    }
}
