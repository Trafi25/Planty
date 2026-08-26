package com.traffipart.polanty.data.mapper

import com.traffipart.polanty.core.model.PlantCandidate
import com.traffipart.polanty.core.model.PlantIdentification
import com.traffipart.polanty.data.remote.plant.dto.PlantNetResponseDto

fun PlantNetResponseDto.toDomain(): PlantIdentification {
    return PlantIdentification(
        bestMatch = bestMatch,
        candidates =
            results.mapNotNull { results ->
                val scientificName =
                    results.species.scientificNameWithoutAuthor
                        ?: results.species.scientificName
                        ?: return@mapNotNull null
                PlantCandidate(
                    scientificName = scientificName,
                    commonName = results.species.commonNames.firstOrNull(),
                    confidence = results.score,
                )
            },
    )
}
