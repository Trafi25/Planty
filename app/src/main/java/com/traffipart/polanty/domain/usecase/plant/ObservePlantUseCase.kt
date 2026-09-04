package com.traffipart.polanty.domain.usecase.plant

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe a single plant by its unique identifier.
 *
 * @property repository The repository to observe the plant from.
 */
class ObservePlantUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
    ) {
        /**
         * Returns a Flow that emits the plant with the specified [plantId] whenever it changes.
         *
         * @param plantId The unique ID of the plant to observe.
         * @return A [kotlinx.coroutines.flow.Flow] of the [com.traffipart.polanty.domain.model.Plant] with the given ID.
         */
        operator fun invoke(plantId: Long): Flow<Plant?> = repository.observePlant(plantId)
    }
