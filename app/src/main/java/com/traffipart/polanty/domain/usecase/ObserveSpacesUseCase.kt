package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe all available plant spaces.
 *
 * @property repository The repository to observe spaces from.
 */
class ObserveSpacesUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        /**
         * Returns a Flow that emits the current list of all plant spaces whenever it changes.
         *
         * @return A [Flow] of a list of [PlantSpace]s.
         */
        operator fun invoke(): Flow<List<PlantSpace>> = repository.observeSpaces()
    }
