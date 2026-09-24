package com.traffipart.polanty.domain.usecase.plant

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe all plants assigned to a specific plant space.
 *
 * @property repository The repository providing plant stream observations.
 */
class ObservePlantsBySpaceUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
    ) {
        /**
         * Returns a Flow that emits the list of plants located in the specified space.
         *
         * @param spaceId The unique identifier of the target plant space.
         * @return A [Flow] emitting lists of [Plant] domain models.
         */
        operator fun invoke(spaceId: Long): Flow<List<Plant>> = repository.observePlantsBySpace(spaceId)
    }
