package com.traffipart.polanty.domain.usecase.space

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import javax.inject.Inject

/**
 * Use case to create a new plant space.
 *
 * This use case handles name normalization and ensures that custom spaces have a valid name.
 * It uses the display name of the [com.traffipart.polanty.domain.model.PlantSpaceType] if no custom name is provided for non-custom types.
 *
 * @property repository The repository to insert the new space into.
 */
class CreateSpaceUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        /**
         * Creates and inserts a new plant space.
         *
         * @param customName The optional name provided by the user.
         * @param type The [com.traffipart.polanty.domain.model.PlantSpaceType] of the new space.
         * @return The unique ID of the newly created space.
         * @throws IllegalArgumentException If a custom space is requested without a valid name.
         */
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
                    normalizeName.isNotEmpty() -> {
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
