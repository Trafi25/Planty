package com.traffipart.polanty.data.mapper

import com.traffipart.polanty.data.remote.plant.dto.PlantNetResponseDto
import com.traffipart.polanty.data.room.plant.PlantEntity
import com.traffipart.polanty.data.room.space.PlantSpaceEntity
import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.domain.model.PlantIdentification
import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.model.PlantSpaceType

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

fun PlantEntity.toDomain(): Plant =
    Plant(
        id = id,
        scientificName = scientificName,
        commonName = commonName,
        nickname = nickname,
        spaceId = spaceId,
        imageUri = imageUri,
    )

fun Plant.toEntity(): PlantEntity =
    PlantEntity(
        id = id,
        scientificName = scientificName,
        commonName = commonName,
        nickname = nickname,
        spaceId = spaceId,
        imageUri = imageUri,
    )

fun PlantSpaceEntity.toDomain(): PlantSpace =
    PlantSpace(
        id = id,
        name = name,
        type = PlantSpaceType.valueOf(type),
    )

fun PlantSpace.toEntity(): PlantSpaceEntity =
    PlantSpaceEntity(
        id = id,
        name = name,
        type = type.name,
    )
