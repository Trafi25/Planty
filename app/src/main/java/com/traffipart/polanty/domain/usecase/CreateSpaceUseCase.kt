package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import javax.inject.Inject

class CreateSpaceUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        suspend operator fun invoke(
            customName: String?,
            type: PlantSpaceType,
        ): Long {
            val normalizeName =
                customName
                    ?.trim()
                    ?.replace("\\s+".toRegex(), " ")
                    .orEmpty()

            val finalName =
                when {
                    type == PlantSpaceType.Custom -> {
                        require(normalizeName.length >= 2) {
                            "Custom space name is required "
                        }
                        normalizeName
                    }
                    normalizeName.isEmpty() -> {
                        normalizeName
                    }
                    else -> type.displayName
                }

            return repository.insertSpace(
                PlantSpace(
                    id = 0,
                    name = finalName,
                    type = type,
                ),
            )
        }
    }
