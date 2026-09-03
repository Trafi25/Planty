package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe the list of all plants in the garden.
 *
 * @property repository The repository to observe plants from.
 */
class ObservePlantsUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
    ) {
        /**
         * Returns a Flow that emits the current list of all plants whenever it changes.
         *
         * @return A [Flow] of a list of [Plant]s.
         */
        operator fun invoke(): Flow<List<Plant>> = repository.observePlants()
    }
