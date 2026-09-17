package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing all care tasks for a specific plant.
 *
 * @property careRepository The repository providing the task stream.
 */
class ObservePlantCareTasksUseCase
    @Inject
    constructor(
        private val careRepository: CareTaskRepository,
    ) {
        /**
         * Returns a Flow that emits the list of care tasks for the given plant whenever it changes.
         *
         * @param plantId The unique identifier of the target plant.
         */
        operator fun invoke(plantId: Long): Flow<List<CareTask>> = careRepository.observePlantTasks(plantId)
    }
