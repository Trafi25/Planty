package com.traffipart.polanty.data.mapper

import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeEntity
import com.traffipart.polanty.domain.model.HumidityRange
import com.traffipart.polanty.domain.model.LightRequirement
import com.traffipart.polanty.domain.model.PlantCareProfile
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.model.PlantSpeciesInfo
import com.traffipart.polanty.domain.model.PlantToxicity
import com.traffipart.polanty.domain.model.TemperatureRange
import com.traffipart.polanty.domain.model.ToxicityLevel
import com.traffipart.polanty.domain.model.WateringProfile

fun PlantKnowledge.toEntity(
    lookupScientificName: String,
): PlantKnowledgeEntity {
    val watering =
        careProfile?.watering

    val humidity =
        careProfile?.humidity

    val temperature =
        careProfile?.temperature

    return PlantKnowledgeEntity(
        lookupScientificName =
            lookupScientificName,

        scientificName =
            speciesInfo.scientificName,

        commonName =
            speciesInfo.commonName,

        description =
            speciesInfo.description,

        origin =
            speciesInfo.origin,

        petToxicity =
            speciesInfo.toxicity.pets.name,

        humanToxicity =
            speciesInfo.toxicity.humans.name,

        toxicityNotes =
            speciesInfo.toxicity.notes,

        heightMinCm =
            speciesInfo.typicalHeightCmMin,

        heightMaxCm =
            speciesInfo.typicalHeightCmMax,

        wateringDaysMin =
            watering?.soilCheckIntervalDaysMin,

        wateringDaysMax =
            watering?.soilCheckIntervalDaysMax,

        wateringInstruction =
            watering?.instruction,

        lightRequirement =
            careProfile?.light?.name,

        humidityMinPercent =
            humidity?.minPercent,

        humidityMaxPercent =
            humidity?.maxPercent,

        temperatureMinCelsius =
            temperature?.minCelsius,

        temperatureMaxCelsius =
            temperature?.maxCelsius,

        fertilizing =
            careProfile?.fertilizing,

        cachedAt =
            System.currentTimeMillis(),
    )
}

fun PlantKnowledgeEntity.toDomain():
        PlantKnowledge {
    val watering =
        if (
            wateringDaysMin != null &&
            wateringDaysMax != null &&
            wateringInstruction != null
        ) {
            WateringProfile(
                soilCheckIntervalDaysMin =
                    wateringDaysMin,
                soilCheckIntervalDaysMax =
                    wateringDaysMax,
                instruction =
                    wateringInstruction,
            )
        } else {
            null
        }

    val light =
        lightRequirement
            ?.let { raw ->
                runCatching {
                    LightRequirement.valueOf(raw)
                }.getOrNull()
            }

    val humidity =
        if (
            humidityMinPercent != null &&
            humidityMaxPercent != null
        ) {
            HumidityRange(
                minPercent =
                    humidityMinPercent,
                maxPercent =
                    humidityMaxPercent,
            )
        } else {
            null
        }

    val temperature =
        if (
            temperatureMinCelsius != null &&
            temperatureMaxCelsius != null
        ) {
            TemperatureRange(
                minCelsius =
                    temperatureMinCelsius,
                maxCelsius =
                    temperatureMaxCelsius,
            )
        } else {
            null
        }

    val careProfile =
        if (
            watering != null ||
            light != null ||
            humidity != null ||
            temperature != null ||
            fertilizing != null
        ) {
            PlantCareProfile(
                scientificName =
                    scientificName,
                watering =
                    watering,
                light =
                    light,
                humidity =
                    humidity,
                temperature =
                    temperature,
                fertilizing =
                    fertilizing,
            )
        } else {
            null
        }

    return PlantKnowledge(
        speciesInfo =
            PlantSpeciesInfo(
                scientificName =
                    scientificName,
                commonName =
                    commonName,
                description =
                    description,
                origin =
                    origin,
                toxicity =
                    PlantToxicity(
                        pets =
                            petToxicity.toToxicityLevel(),
                        humans =
                            humanToxicity.toToxicityLevel(),
                        notes =
                            toxicityNotes,
                    ),
                typicalHeightCmMin =
                    heightMinCm,
                typicalHeightCmMax =
                    heightMaxCm,
            ),
        careProfile =
            careProfile,
    )
}
private fun String.toToxicityLevel():
        ToxicityLevel =
    runCatching {
        ToxicityLevel.valueOf(this)
    }.getOrDefault(
        ToxicityLevel.Unknown,
    )
