package com.traffipart.polanty.data.mapper

import com.traffipart.polanty.data.remote.plant.dto.PlantNetResponseDto
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.domain.model.PlantIdentification

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
