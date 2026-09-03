package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateSpaceUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            type: PlantSpaceType,
        ): Long {
            val normalizeName =
                name
                    .trim()
                    .replace("\\s+".toRegex(), " ")
            require(normalizeName.length >= 2) { "Space name is too short" }

            val existingSpaces = repository.observeSpaces().first()
            val alreadyExists =
                existingSpaces.any { space ->
                    space.name.equals(normalizeName, ignoreCase = true)
                }
            require(!alreadyExists) { "Space already exists" }

            val space = PlantSpace(id = 0, name = normalizeName, type = type)
            return repository.insertSpace(space)
        }
    }
