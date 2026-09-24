package com.traffipart.polanty.domain.usecase.space

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.repository.plant.PlantSpaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe a single plant space by its unique identifier.
 *
 * @property repository The repository providing plant space stream observations.
 */
class ObserveSpaceUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        /**
         * Returns a Flow emitting the [PlantSpace] matching [spaceId], or `null` if deleted.
         *
         * @param spaceId The unique identifier of the plant space to observe.
         * @return A [Flow] emitting [PlantSpace] or `null`.
         */
        operator fun invoke(spaceId: Long): Flow<PlantSpace?> = repository.observeSpace(spaceId)
    }
