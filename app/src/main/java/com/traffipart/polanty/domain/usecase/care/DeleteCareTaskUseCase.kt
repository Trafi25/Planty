package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import javax.inject.Inject

/**
 * Use case for deleting a plant care task.
 *
 * @property careRepository The repository to perform the deletion.
 */
class DeleteCareTaskUseCase
    @Inject
    constructor(
        private val careRepository: CareTaskRepository,
    ) {
        /**
         * Deletes a care task.
         *
         * @param task The [CareTask] domain model to be removed.
         * @return True if the deletion was successful, false otherwise.
         */
        suspend operator fun invoke(task: CareTask): Boolean = careRepository.deleteTask(task)
    }
